package com.bocsoft.obss.common.serializable;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.ExpiredSessionException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.StoppedSessionException;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.ValidatingSession;

import java.beans.Transient;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.*;

/**
 * 序列化session，避免session存redis无法反序列化问题
 * 改造SimpleSession，去除transient修饰
 */
@Slf4j
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class SerializableSession implements ValidatingSession {

    protected static final long MILLIS_PER_SECOND = 1000L;
    protected static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    protected static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;

    private Serializable id;
    private Date startTimestamp;
    private Date stopTimestamp;
    private Date lastAccessTime;
    private long timeout;
    private boolean expired;
    private String host;
    private Map<Object, Object> attributes;

    public SerializableSession() {
        this.timeout = DefaultSessionManager.DEFAULT_GLOBAL_SESSION_TIMEOUT;
        this.startTimestamp = new Date();
        this.lastAccessTime = this.startTimestamp;
    }

    public SerializableSession(String host) {
        this();
        this.host = host;
    }


    @Override
    public void touch() {
        this.lastAccessTime = new Date();
    }

    @Override
    public void stop() {
        if (this.stopTimestamp == null) {
            this.stopTimestamp = new Date();
        }
    }

    @Transient
    @Override
    public Collection<Object> getAttributeKeys() throws InvalidSessionException {
        return attributes == null ? Collections.emptySet() : attributes.keySet();
    }

    @Override
    public Object getAttribute(Object key) {
        return attributes == null ? null : attributes.get(key);
    }

    @Override
    public void setAttribute(Object key, Object value) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        attributes.put(key, value);
    }

    @Override
    public Object removeAttribute(Object key) {
        return attributes == null ? null : attributes.remove(key);
    }

    @Transient
    private boolean isStopped() {
        return this.getStopTimestamp() != null;
    }

    @Transient
    protected void expire() {
        this.stop();
        this.expired = true;
    }

    @Transient
    @Override
    public boolean isValid() {
        return !this.isStopped() && !this.isExpired();
    }

    private boolean isTimedOut() {
        if (this.isExpired()) {
            return true;
        } else {
            long timeout = this.getTimeout();
            if (timeout >= 0L) {
                Date lastAccessTime = this.getLastAccessTime();
                if (lastAccessTime == null) {
                    String msg = "session.lastAccessTime for session with id [" + this.getId() + "] is null.  This value must be set at least once, preferably at least upon instantiation.  Please check the " + this.getClass().getName() + " implementation and ensure this value will be set (perhaps in the constructor?)";
                    throw new IllegalStateException(msg);
                } else {
                    long expireTimeMillis = System.currentTimeMillis() - timeout;
                    Date expireTime = new Date(expireTimeMillis);
                    return lastAccessTime.before(expireTime);
                }
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("No timeout for session with id [" + this.getId() + "].  Session is not considered expired.");
                }
                return false;
            }
        }
    }

    @Override
    public void validate() throws InvalidSessionException {
        if (this.isStopped()) {
            String msg = "Session with id [" + this.getId() + "] has been explicitly stopped.  No further interaction under this session is allowed.";
            throw new StoppedSessionException(msg);
        } else if (this.isTimedOut()) {
            this.expire();
            Date lastAccessTime = this.getLastAccessTime();
            long timeout = this.getTimeout();
            Serializable sessionId = this.getId();
            DateFormat df = DateFormat.getInstance();
            String msg = "Session with id [" + sessionId + "] has expired. Last access time: " + df.format(lastAccessTime) + ".  Current time: " + df.format(new Date()) + ".  Session timeout is set to " + timeout / 1000L + " seconds (" + timeout / 60000L + " minutes)";
            if (log.isTraceEnabled()) {
                log.trace(msg);
            }
            throw new ExpiredSessionException(msg);
        }
    }
}
