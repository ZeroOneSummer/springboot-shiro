package com.bocsoft.obss.common.shiro.config.web;

import com.bocsoft.obss.common.shiro.config.filter.KickoutSessionControlFilter;
import com.bocsoft.obss.common.shiro.config.filter.ShiroAuthenFilter;
import com.bocsoft.obss.common.util.RedisUtil;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.event.EventBus;
import org.apache.shiro.event.support.DefaultEventBus;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 【shiro-config】
 */
@Configuration
@AutoConfigureAfter(ShiroWebAutoConfiguration.class)
public class ShiroConfig {

    /**
     * 过滤器管理
     * ShiroWebAutoConfiguration 注册的bean: sessionManager、securityManager、shiroFilterChainDefinition
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SessionManager sessionManager, SecurityManager securityManager, ShiroFilterChainDefinition shiroFilterChainDefinition) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setLoginUrl("/user/login.html");
        //shiroFilterFactoryBean.setUnauthorizedUrl("/noauth");
        shiroFilterFactoryBean.getFilters().put("authc", new ShiroAuthenFilter());
        shiroFilterFactoryBean.getFilters().put("kickout", kickoutSessionControlFilter(sessionManager));
        //过滤规则
        shiroFilterFactoryBean.setFilterChainDefinitionMap(shiroFilterChainDefinition.getFilterChainMap());
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
     * FormAuthenticationFilter 过滤器 过滤记住我
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
     * 踢人过滤器
     * @param sessionManager
     * @return
     */
    @Bean
    public KickoutSessionControlFilter kickoutSessionControlFilter(SessionManager sessionManager){
        KickoutSessionControlFilter kickoutSessionControlFilter = new KickoutSessionControlFilter(sessionManager, redisUtil(), shiroProperties());
        kickoutSessionControlFilter.setLoginUrl("/user/login.html");  //踢出后重定向url
        kickoutSessionControlFilter.setKickoutAfter(false);  //是否踢出刚登录的
        kickoutSessionControlFilter.setMaxSession(2);  //一个账号允许在线人数
        return kickoutSessionControlFilter;
    }

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

    @Bean
    public ShiroProperties.UserProperties userProperties(){
        return new ShiroProperties.UserProperties();
    }
}