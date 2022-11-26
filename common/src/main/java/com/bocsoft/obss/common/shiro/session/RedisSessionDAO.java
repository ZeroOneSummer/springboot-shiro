package com.bocsoft.obss.common.shiro.session;

import com.bocsoft.obss.common.serializable.RedisPrincipalCollection;
import com.bocsoft.obss.common.serializable.SerializableSession;
import com.bocsoft.obss.common.shiro.config.web.ShiroProperties;
import com.bocsoft.obss.common.util.RedisUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 【session存放在redis缓存中】
 */
@Setter
@Slf4j
public class RedisSessionDAO extends AbstractSessionDAO {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ShiroProperties shiroProperties;

    @Override
    public void update(Session session) throws UnknownSessionException {
        if (session == null || session.getId() == null) {
            log.error("session or session id is null");
            throw new UnknownSessionException("session or session id is null");
        }
        //序列化session，重写SessionFactory使用SerializableSession
        if (session instanceof SerializableSession) {
            SerializableSession serializableSession = (SerializableSession) session;
            Iterator<Object> iterator = serializableSession.getAttributeKeys().iterator();
            while (iterator.hasNext()){
                Object key = iterator.next();
                Object value = serializableSession.getAttribute(key);
                if (value instanceof PrincipalCollection){
                    serializableSession.setAttribute(key, new RedisPrincipalCollection((PrincipalCollection) value));
                }
            }
            redisUtil.set(shiroProperties.getSessionPrefix() + session.getId(), session, (session.getTimeout() / 1000L));
        }
    }

    @Override
    public void delete(Session session) {
        if (session == null || session.getId() == null) {
            log.error("session or session id is null");
            return;
        }
        try {
            redisUtil.del(shiroProperties.getSessionPrefix() + session.getId());
        } catch (SerializationException e) {
            log.error("delete session error. session id=" + session.getId());
        }
    }

    @Override
    public Collection<Session> getActiveSessions() {
        Set<Session> sessions = new HashSet<>();
        try {
            Set<String> keys = redisUtil.scan(shiroProperties.getSessionPrefix() + "*");
            if (keys != null && keys.size() > 0) {
                for (String key : keys) {
                    Session session = (Session) redisUtil.get(key);
                    sessions.add(session);
                }
            }
        } catch (SerializationException e) {
            log.error("get active sessions error.");
        }
        return sessions;
    }

    @Override
    protected Serializable doCreate(Session session) {
        if (session == null) {
            log.error("session is null");
            throw new UnknownSessionException("session is null");
        }
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        session.setTimeout(shiroProperties.getSessionTimeout() * 60 * 1000);
        update(session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        if (sessionId == null) {
            log.warn("session id is null");
            return null;
        }
        Session session = null;
        try {
            session = (Session) redisUtil.get(shiroProperties.getSessionPrefix() + sessionId);
        } catch (SerializationException e) {
            log.error("read session error. settionId=" + sessionId);
        }
        return session;
    }

    @Override
    protected void assignSessionId(Session session, Serializable sessionId) {
        ((SerializableSession)session).setId(sessionId);
    }
}