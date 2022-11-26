package com.bocsoft.obss.common.shiro.session;

import com.bocsoft.obss.common.serializable.SerializableSession;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionFactory;

/**
 * 工厂创建session
 * 使用可反序列化的session
 */
public class RedisSessionFactory implements SessionFactory {

    @Override
    public Session createSession(SessionContext initData) {
        if (initData != null) {
            String host = initData.getHost();
            if (host != null) {
                return new SerializableSession(host);
            }
        }
        return new SerializableSession();
    }
}