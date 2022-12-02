package com.bocsoft.obss.shiro.config;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

/**
 * 测试时专用配置, 用户非shiro模块的登录鉴权
 */
@TestConfiguration
public class ShiroConfig {

//    @Bean
//    @ConditionalOnMissingBean
//    public CacheManager cacheManager() {
//        return new MemoryConstrainedCacheManager();
//    }
//
//    @Bean
//    @ConditionalOnMissingBean
//    public SessionDAO sessionDAO() {
//        MemorySessionDAO sessionDAO = new MemorySessionDAO();
//        sessionDAO.setSessionIdGenerator(session -> UUID.randomUUID().toString());
//        return sessionDAO;
//    }
//
//    @Bean
//    public SessionsSecurityManager securityManager() {
//        SessionsSecurityManager webSessionManager = new DefaultWebSecurityManager();
//        webSessionManager.setRealm(new TestRealm());
//        return webSessionManager;
//    }
}
