package com.bocsoft.obss.common.shiro.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * shiro自定义属性
 */
@Setter
@Getter
@Component
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
}
