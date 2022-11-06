package com.bocsoft.obss.shiro.redis;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * redisManager
 *  shiro Cache -> RedisCache(redisTemplate) -> RedisCacheManager(shiro CacheManager) -> SecurityManager
 */
@Slf4j
public class RedisCacheManager implements CacheManager {
    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<>();

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        log.debug("get cache, name={}", name);
        Cache cache = caches.get(name);
        if (cache == null) {
            cache = new RedisCache<K, V>();
            caches.put(name, cache);
        }
        return cache;
    }
}