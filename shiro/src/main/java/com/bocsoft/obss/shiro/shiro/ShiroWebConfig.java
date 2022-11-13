package com.bocsoft.obss.shiro.shiro;

import org.springframework.context.annotation.Configuration;

/**
 * 此配置可用用单体工程，不适合微服务工程
 */

@Deprecated
@Configuration
public class ShiroWebConfig {
//
//    /**
//     * shiro-thymeleaf方言
//     */
//    @Bean
//    public ShiroDialect shiroDialect() {
//        return new ShiroDialect();
//    }
//
//    /**
//     * 开启shiro aop注解支持
//     */
//    @Bean
//    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager){
//        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
//        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
//        return authorizationAttributeSourceAdvisor;
//    }
//
//    /**
//     * 开启cglib代理，由Advisor决定对哪些类的方法进行AOP代理
//     */
//    @Bean
//    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
//        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
//        creator.setProxyTargetClass(true);
//        return creator;
//    }
//
//    /**
//     * 过滤器管理
//     */
//    @Bean
//    public ShiroFilterFactoryBean getShiroFilterFactoryBean(SecurityManager  securityManager) {
//        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
//        //给filter设置安全管理器
//        shiroFilterFactoryBean.setSecurityManager(securityManager);
//        //自定义拦截器限制并发人数,参考博客：
//        LinkedHashMap<String, Filter> filtersMap = new LinkedHashMap<>();
//        //限制同一帐号同时在线的个数
//        filtersMap.put("kickout", kickoutSessionControlFilter());
//        shiroFilterFactoryBean.setFilters(filtersMap);
//        //配置系统资源
//        Map<String, String> map = new HashMap<>();
//        //不需要拦截
//        map.put("/user/login.html", "anon");
//        map.put("/user/register.html", "anon");
//        map.put("/user/login", "anon");
//        map.put("/user/register", "anon");
//        //不需要拦截swagger
//        map.put("/swagger/**", "anon");
//        map.put("/v2/api-docs", "anon");
//        map.put("/swagger-ui.html", "anon");
//        map.put("/swagger-ui.html#", "anon");
//        map.put("/swagger-resources/**", "anon");
//        //静态资源
//        map.put("/css/**", "anon");
//        map.put("/js/**", "anon");
//        map.put("/img/**", "anon");
//        map.put("/webjars/**", "anon");
//        map.put("/favicon.ico", "anon");
//        map.put("/captcha.jpg", "anon");
//        //存入不需要拦截druid sql监控
//        map.put("/druid/**", "anon");
//        //h2
//        map.put("/h2/**", "anon");
//        //必须放到最后
//        //如果开启限制同一账号登录,改为 .put("/**", "kickout,user");
//        map.put("/**", "authc,kickout,user");
//        //默认认证界面路径
//        shiroFilterFactoryBean.setLoginUrl("/user/login.html");
////        shiroFilterFactoryBean.setUnauthorizedUrl("/noauth");
//        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
//        return shiroFilterFactoryBean;
//    }
//
//    @Bean
//    public SecurityManager securityManager() {
//        DefaultWebSecurityManager sessionManager = new DefaultWebSecurityManager();
//        //realm
//        sessionManager.setRealm(realm());
//        //remember me
//        sessionManager.setRememberMeManager(rememberMeManager());
//        //session
//        sessionManager.setSessionManager(sessionManager());
//        //cache
//        sessionManager.setCacheManager(new RedisCacheManager());
//        return sessionManager;
//    }
//
//    @Bean
//    public Realm realm() {
//        CustomRealm customerRealm = new CustomRealm();
//        // 设置密码匹配器
//        customerRealm.setCredentialsMatcher(hashedCredentialsMatcher());
//        // 设置缓存管理器
//        customerRealm.setCacheManager(new RedisCacheManager());
//        customerRealm.setCachingEnabled(true);  // 开启全局缓存
//        customerRealm.setAuthenticationCachingEnabled(true);    // 开启认证缓存并指定缓存名称
//        customerRealm.setAuthorizationCachingEnabled(true);     // 开启授权缓存并指定缓存名称
//        return customerRealm;
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
//    /**
//     * 并发登录控制
//     */
//    @Bean
//    public KickoutSessionControlFilter kickoutSessionControlFilter(){
//        KickoutSessionControlFilter kickoutSessionControlFilter = new KickoutSessionControlFilter();
//        //用于根据会话ID，获取会话进行踢出操作的；
//        kickoutSessionControlFilter.setSessionManager(sessionManager());
//        //是否踢出后来登录的，默认是false；即后者登录的用户踢出前者登录的用户；
//        kickoutSessionControlFilter.setKickoutAfter(false);
//        //同一个用户最大的会话数，默认1；比如2的意思是同一个用户允许最多同时两个人登录；
//        kickoutSessionControlFilter.setMaxSession(1);
//        //被踢出后重定向到的地址；
//        kickoutSessionControlFilter.setKickoutUrl("/login?kickout=1");
//        return kickoutSessionControlFilter;
//    }
//
//    /**
//     * 让某个实例的某个方法的返回值注入为Bean的实例
//     * Spring静态注入
//     */
//    @Bean
//    public MethodInvokingFactoryBean getMethodInvokingFactoryBean(){
//        MethodInvokingFactoryBean factoryBean = new MethodInvokingFactoryBean();
//        factoryBean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
//        factoryBean.setArguments(new Object[]{securityManager()});
//        return factoryBean;
//    }
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
//    /**
//     * 配置会话管理器
//     */
//    @Bean
//    public RedisSessionDAO redisSessionDAO() {
//        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
//        redisSessionDAO.setRedisUtil(redisUtil());
//        return redisSessionDAO;
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
