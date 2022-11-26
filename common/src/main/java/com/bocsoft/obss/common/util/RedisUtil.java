package com.bocsoft.obss.common.util;

import org.apache.shiro.dao.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis工具类
 */
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private ValueOperations<String, Object> valueOperations;

    private HashOperations<String, String, Object> hashOperations;

    @PostConstruct
    private void initOps(){
        valueOperations = redisTemplate.opsForValue();
        hashOperations = redisTemplate.opsForHash();
    }

    //=============================== valueOperations =======================================
    /**
     * 普通缓存获取
     */
    public Object get(String key) {
        return valueOperations.get(key);
    }

    /**
     * 普通缓存放入
     */
    public void set(String key, Object value) {
        valueOperations.set(key, value);
    }

    /**
     * 普通缓存放入并设置时间
     */
    public void set(String key, Object value, long time) {
        if (time > 0) {
            valueOperations.set(key, value, time, TimeUnit.SECONDS);
        } else {
            set(key, value);
        }
    }


    //=============================== hashOperations =======================================
    public void hashPutAll(String key, Map<? extends String, ?>val){
        hashOperations.putAll(key, val);
    }

    public void hashPut(String key, String hkey, Object val){
        hashOperations.put(key, hkey, val);
    }

    public Object hashGet(String key, Object val){
        return hashOperations.get(key, val);
    }

    public Boolean hasHashKey(String key, Object val){
        return hashOperations.hasKey(key, val);
    }

    public Set<String> hashKeys(String key){
        return hashOperations.keys(key);
    }


    //=============================== redisTemplate =======================================
    /**
     * 指定缓存失效时间
     */
    public void expire(String key, long time) {
        redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 删除缓存
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    /**
     * 批量删除key
     */
    public void del(Collection keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 使用scan命令 查询某些前缀的key
     */
    public Set<String> scan(String key) {
        Set<String> execute = this.redisTemplate.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {
                Set<String> binaryKeys = new HashSet<>();
                Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder().match(key).count(1000).build());
                while (cursor.hasNext()) {
                    binaryKeys.add(new String(cursor.next()));
                }
                return binaryKeys;
            }
        });
        return execute;
    }

    /**
     * 使用scan命令 查询某些前缀的key有多少个
     * 用来获取当前session数量,也就是在线用户
     */
    public Long scanSize(String key) {
        long dbSize = this.redisTemplate.execute((RedisCallback<Long>) connection -> {
            long count = 0L;
            Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(key).count(1000).build());
            while (cursor.hasNext()) {
                cursor.next();
                count++;
            }
            return count;
        });
        return dbSize;
    }
}