package com.bocsoft.obss.shiro.redis;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Setter
@Slf4j
public class RedisSessionDAO extends AbstractSessionDAO {

    private String keyPrefix = "shiro:session:";
    private static final RedisSerializer redisSerializer = new SerializeUtils();

    //@Autowired
    private RedisUtil redisUtil;

    @Override
    public void update(Session session) throws UnknownSessionException {
        if (session == null || session.getId() == null) {
            log.error("session or session id is null");
            throw new UnknownSessionException("session or session id is null");
        }
        redisUtil.set(keyPrefix + session.getId(), redisSerializer.serialize(session), (session.getTimeout() / 1000L));
    }

    @Override
    public void delete(Session session) {
        if (session == null || session.getId() == null) {
            log.error("session or session id is null");
            return;
        }
        try {
            redisUtil.del(keyPrefix + session.getId());
        } catch (SerializationException e) {
            log.error("delete session error. session id=" + session.getId());
        }
    }

    @Override
    public Collection<Session> getActiveSessions() {
        Set<Session> sessions = new HashSet<>();
        try {
            Set<String> keys = redisUtil.scan(keyPrefix + "*");
            if (keys != null && keys.size() > 0) {
                for (String key : keys) {
                    Session session = (Session) redisSerializer.deserialize((byte[])redisUtil.get(key));
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
        super.assignSessionId(session, sessionId);
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
        log.debug("read session from redis");
        try {
            session = (Session) redisSerializer.deserialize((byte[])redisUtil.get(keyPrefix + sessionId));
        } catch (SerializationException e) {
            log.error("read session error. settionId=" + sessionId);
        }
        return session;
    }
}