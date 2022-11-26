//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bocsoft.obss.common.shiro.config.web;

import com.bocsoft.obss.common.redis.RedisCacheManager;
import com.bocsoft.obss.common.shiro.session.RedisSessionDAO;
import com.bocsoft.obss.common.shiro.session.RedisSessionFactory;
import com.bocsoft.obss.common.shiro.session.ShiroSessionListener;
import com.bocsoft.obss.common.shiro.session.ShiroSessionManager;
import com.bocsoft.obss.common.util.RedisUtil;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.web.ShiroUrlPathHelper;
import org.apache.shiro.spring.web.config.AbstractShiroWebConfiguration;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * 参考 shiro-spring-boot-web-starter 的 spring.factories
 * 【shiro-web-config】
 */
@Configuration
@ConditionalOnProperty(name = {"shiro.enabled"}, matchIfMissing = true) //没匹配到，默认true
public class ShiroWebAutoConfiguration extends AbstractShiroWebConfiguration {

    @Autowired
    private RedisUtil redisUtil;

    @Bean
    @ConditionalOnMissingBean(name = "authorizer") //不加authorizer会报找不到
    @Override
    protected Authorizer authorizer() {
        return super.authorizer();
    }

    @Bean
    @ConditionalOnMissingBean
    @Override
    protected SessionDAO sessionDAO() {
        if (redisUtil != null){
            RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
            redisSessionDAO.setSessionIdGenerator(new JavaUuidSessionIdGenerator());
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
        //设置自定义SessionFactory
        sessionManager.setSessionFactory(new RedisSessionFactory());
        //配置监听
        sessionManager.setSessionListeners(Collections.singletonList(new ShiroSessionListener()));
        //禁用cookie，不向浏览器发送cookie
        sessionManager.setSessionIdCookieEnabled(false);
        //默认是MemorySessionDAO，分布式session需要自己重写
        sessionManager.setSessionDAO(sessionDAO());
        //redis session
        sessionManager.setCacheManager(new RedisCacheManager());
        //是否开启删除无效的session对象  默认为true
        sessionManager.setDeleteInvalidSessions(sessionManagerDeleteInvalidSessions);
        //是否开启定时调度器进行检测过期session 默认为true
        sessionManager.setSessionValidationSchedulerEnabled(true);
        //设置session失效的扫描时间, 清理用户直接关闭浏览器造成的孤立会话 默认为 1个小时
        //设置该属性 就不需要设置 ExecutorServiceSessionValidationScheduler 底层也是默认自动调用ExecutorServiceSessionValidationScheduler
        //暂时设置为 5秒 用来测试
        sessionManager.setSessionValidationInterval(60*60*1000);
        //防止首次访问url携带jessionId错误
        sessionManager.setSessionIdUrlRewritingEnabled(false);
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
        chainDefinition.addPathDefinition("/webjars/**", "anon");
        chainDefinition.addPathDefinition("/css/**", "anon");
        chainDefinition.addPathDefinition("/js/**", "anon");
        chainDefinition.addPathDefinition("/img/**", "anon");
        chainDefinition.addPathDefinition("/favicon.ico", "anon");
        chainDefinition.addPathDefinition("/captcha.jpg", "anon");
        //druid监控界面
        chainDefinition.addPathDefinition("/druid/**", "anon");
        //h2数据库管理界面
        chainDefinition.addPathDefinition("/h2/**", "anon");
        //必须放到最后, kickout开启限制同一账号登录
        chainDefinition.addPathDefinition("/**", "authc,kickout");
        return chainDefinition;
    }

    @Bean
    @ConditionalOnMissingBean
    @Override
    protected ShiroUrlPathHelper shiroUrlPathHelper() {
        return super.shiroUrlPathHelper();
    }
}
