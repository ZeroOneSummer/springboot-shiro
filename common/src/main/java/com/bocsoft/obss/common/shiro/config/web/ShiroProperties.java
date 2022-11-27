package com.bocsoft.obss.common.shiro.config.web;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

/**
 * shiro自定义属性
 */
@Setter
@Getter
public class ShiroProperties {

    @Value("${shiro.enabled}")
    private boolean shiroEnabled;

    @Value("${shiro.annotation.enabled}")
    private boolean annotationEnabled;

    @Value("${shiro.session.prefix}")
    private String sessionPrefix;

    @Value("${shiro.session.timeout}")
    private long sessionTimeout;

    @Value("${shiro.cache.prefix}")
    private String cachePrefix;

    @Value("${shiro.cache.timeout}")
    private long cacheTimeout;

    @Value("${shiro.lock.prefix}")
    private String lockPrefix;

    @Value("${shiro.lock.timeout}")
    private long lockTimeout;

    @Value("${shiro.kickout.prefix}")
    private String kickoutPrefix;

    /**
     * 用户配置
     */
    @Setter
    @Getter
    public static class UserProperties{
        @Value("${shiro.user.error-limit:5}")
        private long errorLimit;

        @Value("${shiro.user.repetition:3}")
        private long repetition;

        @Value("${shiro.user.overdue-day:30}")
        private long overdueDay;
    }
}
