package com.bocsoft.obss.common.shiro.config;

import com.bocsoft.obss.common.shiro.session.RedisSessionDAO;
import com.bocsoft.obss.common.shiro.session.ShiroSessionManager;
import com.bocsoft.obss.common.util.RedisUtil;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.config.ShiroAnnotationProcessorConfiguration;
import org.apache.shiro.spring.web.config.AbstractShiroWebConfiguration;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.subject.WebSubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.UUID;

@ConditionalOnClass(WebSubject.class)
@ConditionalOnProperty(name = "shiro.enabled", matchIfMissing = true) //没匹配到，默认true
//@AutoConfigureBefore({ShiroConfig.class, ShiroAnnotationProcessorConfiguration.class})
@Configuration
//@AutoConfigureAfter({})
public class ShiroWebConfig extends AbstractShiroWebConfiguration {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ShiroProperties shiroProperties;

    @Bean
    @ConditionalOnMissingBean
    @Override
    protected SessionDAO sessionDAO() {
        if (redisUtil != null){
            RedisSessionDAO redisSessionDAO = new RedisSessionDAO(redisUtil, shiroProperties);
            redisSessionDAO.setSessionIdGenerator(session -> UUID.randomUUID().toString());
            return redisSessionDAO;
        }
        return super.sessionDAO();
    }

    @Bean
    @ConditionalOnMissingBean
    @Override
    protected SessionManager sessionManager() {
        //重写getSessionId方法，优先从header拿
        ShiroSessionManager sessionManager = new ShiroSessionManager();
        //禁用cookie，不向浏览器发送cookie
        sessionManager.setSessionIdCookieEnabled(false);
        //默认是MemorySessionDAO，分布式session需要自己重写
        sessionManager.setSessionDAO(sessionDAO());
        sessionManager.setDeleteInvalidSessions(sessionManagerDeleteInvalidSessions);
        return sessionManager;
    }

    @Bean
    @ConditionalOnMissingBean
    @Override
    protected SessionsSecurityManager securityManager(List<Realm> realms) {
        return super.securityManager(realms);
    }

    @Bean
    @ConditionalOnMissingBean
    @Override
    protected ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        //直接放行
        chainDefinition.addPathDefinition("/user/login.html", "anon");
        chainDefinition.addPathDefinition("/user/register.html", "anon");
        chainDefinition.addPathDefinition("/user/login", "anon");
        chainDefinition.addPathDefinition("/user/register", "anon");
        //swagger
        chainDefinition.addPathDefinition("/swagger/**", "anon");
        chainDefinition.addPathDefinition("/v2/api-docs", "anon");
        chainDefinition.addPathDefinition("/swagger-ui.html", "anon");
        chainDefinition.addPathDefinition("/swagger-ui.html#", "anon");
        chainDefinition.addPathDefinition("/swagger-resources/**", "anon");
        //静态资源
        chainDefinition.addPathDefinition("/css/**", "anon");
        chainDefinition.addPathDefinition("/js/**", "anon");
        chainDefinition.addPathDefinition("/img/**", "anon");
        chainDefinition.addPathDefinition("/webjars/**", "anon");
        chainDefinition.addPathDefinition("/favicon.ico", "anon");
        chainDefinition.addPathDefinition("/captcha.jpg", "anon");
        //druid监控界面
        chainDefinition.addPathDefinition("/druid/**", "anon");
        //h2数据库管理界面
        chainDefinition.addPathDefinition("/h2/**", "anon");
        //必须放到最后
        chainDefinition.addPathDefinition("/**", "authc");
        //如果开启限制同一账号登录,改为 .addPathDefinition("/**", "kickout,user");
        return chainDefinition;
    }




}
