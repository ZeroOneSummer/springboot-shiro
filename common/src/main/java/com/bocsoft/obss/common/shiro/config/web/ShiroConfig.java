package com.bocsoft.obss.common.shiro.config.web;

import com.bocsoft.obss.common.shiro.config.filter.ShiroAuthenFilter;
import com.bocsoft.obss.common.util.RedisUtil;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.event.EventBus;
import org.apache.shiro.event.support.DefaultEventBus;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import java.util.Properties;

/**
 * 【shiro-config】
 */
@Configuration
public class ShiroConfig {

    /**
     * 过滤器管理
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setLoginUrl("/user/login.html");
        //shiroFilterFactoryBean.setUnauthorizedUrl("/noauth");
        //shiroFilterFactoryBean.getFilters().put("authc", new ShiroAuthenFilter());
        return shiroFilterFactoryBean;
    }

    /**
     * 记住我-cookie
     */
    @Bean
    public CookieRememberMeManager rememberMeManager(){
        //cookie管理器
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        //cookie的名字
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        //设置有效期时间7天
        simpleCookie.setMaxAge(7*24*60*60);
        //防止xss读取cookie
        simpleCookie.setHttpOnly(true);
        cookieRememberMeManager.setCookie(simpleCookie);
        //指定加密算法，不然每次都不一样，byteSize=16 24 32
        cookieRememberMeManager.setCipherKey(Base64.decode("6ZmI6I2j5Y+R5aSn5ZOlAA=="));
        return cookieRememberMeManager;
    }

    /**
     * ShiroAuthenFilter生效
     */
    @Bean
    public FormAuthenticationFilter formAuthenticationFilter(){
        ShiroAuthenFilter shiroAuthenFilter = new ShiroAuthenFilter();
        //对应前端的checkbox的name = rememberMe
        shiroAuthenFilter.setRememberMeParam("rememberMe");
        return shiroAuthenFilter;
    }

    /**
     * 异常解析跳转
     */
    /*@Bean
    public SimpleMappingExceptionResolver resolver() {
        SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();
        Properties properties = new Properties();
        properties.setProperty("org.apache.shiro.authz.UnauthorizedException", "error/403");
        exceptionResolver.setExceptionMappings(properties);
        return exceptionResolver;
    }*/

    @Bean
    public EventBus eventBus() {
        return new DefaultEventBus();
    }

    /**
     * 配置Shiro生命周期处理器（必须）
     * 防止@Autowired注入为null
     */
    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 自定义bean, 亦可加入CommonAutoConfig内公共注入
     */
    @Bean
    public RedisUtil redisUtil() {
        return new RedisUtil();
    }

    @Bean
    public ShiroProperties shiroProperties() {
        return new ShiroProperties();
    }
}
