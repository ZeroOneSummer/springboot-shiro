package com.bocsoft.obss.common.shiro.config.web;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.config.AbstractShiroAnnotationProcessorConfiguration;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 【开启shiro的权限注解】
 */
@Configuration
@ConditionalOnProperty(name = "shiro.annotation.enabled", matchIfMissing = true) //没匹配到，默认true
public class ShiroAnnotationConfig extends AbstractShiroAnnotationProcessorConfiguration {

    /**
     * 开启shiro aop注解支持
     */
    @Bean
    @ConditionalOnMissingBean
    @Override
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
        //ShiroWebConfig 的 SessionsSecurityManager
        return super.authorizationAttributeSourceAdvisor(securityManager);
    }

    /**
     * 开启cglib代理，由Advisor决定对哪些类的方法进行AOP代理
     */
    @Bean
    @ConditionalOnMissingBean
    @Override
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = super.defaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }
}
