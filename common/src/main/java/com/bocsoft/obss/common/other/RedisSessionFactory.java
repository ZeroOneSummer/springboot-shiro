package com.bocsoft.obss.common.other;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.session.mgt.SimpleSession;

/**
 * 使用SimpleSession防止redis反序列化Session失败
 */
@Deprecated
public class RedisSessionFactory /*implements SessionFactory*/ {

//    @Override
//    public Session createSession(SessionContext sessionContext) {
//        if (sessionContext != null){
//            String host = sessionContext.getHost();
//            if (host != null) {
//                return new SimpleSession(host);
//            }
//
//        }
//        return new SimpleSession();
//    }
}
