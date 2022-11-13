package com.bocsoft.obss.common.shiro.config;

import com.bocsoft.obss.common.redis.RedisCacheManager;
import com.bocsoft.obss.common.shiro.session.RedisSessionDAO;
import com.bocsoft.obss.common.shiro.constant.ShiroConstant;
import com.bocsoft.obss.common.shiro.session.ShiroSessionListener;
import com.bocsoft.obss.common.util.RedisUtil;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import java.util.*;

@Deprecated
@Configuration
public class ShiroConfig {



//    /**
//     * 过滤器管理
//     */
//    @Bean
//    public ShiroFilterFactoryBean getShiroFilterFactoryBean(SecurityManager  securityManager) {
//        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
//        //给filter设置安全管理器
//        shiroFilterFactoryBean.setSecurityManager(securityManager);
//        shiroFilterFactoryBean.setLoginUrl("/user/login.html");
////        shiroFilterFactoryBean.setUnauthorizedUrl("/noauth");
//        return shiroFilterFactoryBean;
//    }
//
//    @Bean
//    public SecurityManager securityManager(Collection<Realm> realms) {
//        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
//        //realm，继承了AuthorizingRealm的会自动注入进来
//        securityManager.setRealms(realms);
//        //remember me
//        securityManager.setRememberMeManager(rememberMeManager());
//        //sessionS
//        securityManager.setSessionManager(sessionManager());
//        //cache
//        securityManager.setCacheManager(new RedisCacheManager());
//        return securityManager;
//    }
//
//    /**
//     * 配置密码比较器
//     */
//    @Bean
//    public HashedCredentialsMatcher hashedCredentialsMatcher(){
//        HashedCredentialsMatcher hashedCredentialsMatcher  = new HashedCredentialsMatcher();
//        hashedCredentialsMatcher .setHashAlgorithmName(ShiroConstant.HASH_ALGORITHM_NAME);
//        hashedCredentialsMatcher .setHashIterations(ShiroConstant.HASH_ITERATORS);
//        //是否存储为16进制
//        hashedCredentialsMatcher .setStoredCredentialsHexEncoded(true);
//        return hashedCredentialsMatcher ;
//    }
//
//    /**
//     * 记住我-cookie
//     */
//    @Bean
//    public CookieRememberMeManager rememberMeManager(){
//        //cookie管理器
//        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
//        //cookie的名字
//        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
//        //设置有效期时间7天
//        simpleCookie.setMaxAge(7*24*60*60);
//        //防止xss读取cookie
//        simpleCookie.setHttpOnly(true);
//        cookieRememberMeManager.setCookie(simpleCookie);
//        //指定加密算法，不然每次都不一样，byteSize=16 24 32
//        cookieRememberMeManager.setCipherKey(Base64.decode("6ZmI6I2j5Y+R5aSn5ZOlAA=="));
//        return cookieRememberMeManager;
//    }
//
//    /**
//     * FormAuthenticationFilter 过滤器 过滤记住我
//     */
//    @Bean
//    public FormAuthenticationFilter formAuthenticationFilter(){
//        FormAuthenticationFilter formAuthenticationFilter = new FormAuthenticationFilter();
//        //对应前端的checkbox的name = rememberMe
//        formAuthenticationFilter.setRememberMeParam("rememberMe");
//        return formAuthenticationFilter;
//    }
//
//    /**
//     * 配置Shiro生命周期处理器
//     * 防止@Autowired注入为null
//     */
//    @Bean(name = "lifecycleBeanPostProcessor")
//    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
//        return new LifecycleBeanPostProcessor();
//    }
//
//    /**
//     * 异常解析跳转
//     */
//    @Bean
//    public SimpleMappingExceptionResolver resolver() {
//        SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();
//        Properties properties = new Properties();
//        properties.setProperty("org.apache.shiro.authz.UnauthorizedException", "error/403");
//        exceptionResolver.setExceptionMappings(properties);
//        return exceptionResolver;
//    }
//
//
//    /**
//     * 会话ID生成器
//     */
//    @Bean
//    public SessionIdGenerator sessionIdGenerator() {
//        return new JavaUuidSessionIdGenerator();
//    }
//
//    /**
//     * redis工具类
//     */
//    @Bean
//    public RedisUtil redisUtil() {
//        return new RedisUtil();
//    }
//
//    @Bean
//    public SessionManager sessionManager() {
//        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
//        //配置监听
//        sessionManager.setSessionListeners(Collections.singletonList(new ShiroSessionListener()));
////        sessionManager.setSessionIdCookie(sessionIdCookie());
//        sessionManager.setSessionDAO(redisSessionDAO());
//        sessionManager.setCacheManager(new RedisCacheManager());
////        sessionManager.setSessionFactory(null);
//        //全局会话超时时间（单位毫秒），默认30分钟  暂时设置为10秒钟 用来测试
//        sessionManager.setGlobalSessionTimeout(1800000);
//        //是否开启删除无效的session对象  默认为true
//        sessionManager.setDeleteInvalidSessions(true);
//        //是否开启定时调度器进行检测过期session 默认为true
//        sessionManager.setSessionValidationSchedulerEnabled(true);
//        //设置session失效的扫描时间, 清理用户直接关闭浏览器造成的孤立会话 默认为 1个小时
//        //设置该属性 就不需要设置 ExecutorServiceSessionValidationScheduler 底层也是默认自动调用ExecutorServiceSessionValidationScheduler
//        //暂时设置为 5秒 用来测试
//        sessionManager.setSessionValidationInterval(3600000);
//        //防止首次访问url携带jessionId错误
//        sessionManager.setSessionIdUrlRewritingEnabled(false);
//        return sessionManager;
//    }
}
