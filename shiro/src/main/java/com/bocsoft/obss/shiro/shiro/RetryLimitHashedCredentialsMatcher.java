package com.bocsoft.obss.shiro.shiro;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bocsoft.obss.shiro.entity.UserBean;
import com.bocsoft.obss.shiro.mapper.UserMapper;
import com.bocsoft.obss.shiro.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 登陆次数限制
 */
@Slf4j
public class RetryLimitHashedCredentialsMatcher extends SimpleCredentialsMatcher {
    public static final String DEFAULT_RETRYLIMIT_CACHE_KEY_PREFIX = "shiro:cache:retrylimit:";
    private String keyPrefix = DEFAULT_RETRYLIMIT_CACHE_KEY_PREFIX;

    @Autowired
    private UserMapper userMapper;

    private RedisUtil redisManager;

    public void setRedisManager(RedisUtil redisManager) {
        this.redisManager = redisManager;
    }

    private String getRedisKickoutKey(String username) {
        return this.keyPrefix + username;
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        //获取用户名
        String username = (String) token.getPrincipal();
        //获取用户登录次数
        AtomicInteger retryCount = (AtomicInteger) redisManager.get(getRedisKickoutKey(username));
        if (retryCount == null) {
            //如果用户没有登陆过,登陆次数加1 并放入缓存
            retryCount = new AtomicInteger(0);
        }
        if (retryCount.incrementAndGet() > 5) {
            //如果用户登陆失败次数大于5次 抛出锁定用户异常并修改数据库字段
            LambdaQueryWrapper<UserBean> query = Wrappers.lambdaQuery();
            query.eq(UserBean::getUsername, username);
            UserBean user = userMapper.selectOne(query);
            if (user != null && 0 == user.getStatus()) {
                //数据库字段默认为0正常状态, 要改为1为锁定
                user.setStatus(1);
                userMapper.updateById(user);
            }
            log.info("锁定用户: {}", user.getUsername());
            //抛出用户锁定异常
            throw new LockedAccountException();
        }
        //判断用户账号和密码是否正确
        boolean matches = super.doCredentialsMatch(token, info);
        if (matches) {
            //如果正确, 从缓存中将用户登录计数清除
            redisManager.del(getRedisKickoutKey(username));
        }
        {
            redisManager.set(getRedisKickoutKey(username), retryCount);
        }
        return matches;
    }

    /**
     * 根据用户名 解锁用户
     */
    public void unlockAccount(String username) {
        LambdaQueryWrapper<UserBean> query = Wrappers.lambdaQuery();
        query.eq(UserBean::getUsername, username);
        UserBean user = userMapper.selectOne(query);
        if (user != null) {
            //修改数据库的状态字段为锁定
            user.setStatus(0);
            userMapper.updateById(user);
            redisManager.del(getRedisKickoutKey(username));
        }
    }
}