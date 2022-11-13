package com.bocsoft.obss.common.shiro.config;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.config.AbstractShiroAnnotationProcessorConfiguration;
import org.apache.shiro.spring.config.ShiroAnnotationProcessorConfiguration;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.subject.WebSubject;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * shiro注解配置类
 */
@ConditionalOnProperty(name = "shiro.annotation.enabled", matchIfMissing = true) //没匹配到，默认true
//@AutoConfigureBefore({ShiroWebConfig.class, ShiroConfig.class, ShiroAnnotationProcessoConfiguration.class})
@Configuration
@AutoConfigureAfter({ShiroWebConfig.class})
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
