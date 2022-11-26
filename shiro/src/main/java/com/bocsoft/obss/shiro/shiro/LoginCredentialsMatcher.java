package com.bocsoft.obss.shiro.shiro;

import com.bocsoft.obss.common.enums.UserStatusEnum;
import com.bocsoft.obss.common.shiro.config.web.ShiroProperties;
import com.bocsoft.obss.common.shiro.constant.ShiroConstant;
import com.bocsoft.obss.common.util.RedisUtil;
import com.bocsoft.obss.shiro.entity.LoginTokenBean;
import com.bocsoft.obss.shiro.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 密码匹配器：登陆密码错误过多，锁定用户
 */
@Slf4j
@Component
public class LoginCredentialsMatcher extends HashedCredentialsMatcher {

    private String userCode;
    private String bankNo;
    static ShiroProperties shiroProperties;
    static ShiroProperties.UserProperties userProperties;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    public void setShiroProperties(ShiroProperties shiroProperties) {
        LoginCredentialsMatcher.shiroProperties = shiroProperties;
    }

    @Autowired
    public void setUserProperties(ShiroProperties.UserProperties userProperties) {
        LoginCredentialsMatcher.userProperties = userProperties;
    }

    //锁定前缀
    public static String getLockKey(String userCode, String bankNo) {
        //shiro:lock:105_556677
        return shiroProperties.getLockPrefix() + bankNo + "_" + userCode;
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        AtomicInteger retryCount = null;
        if (token instanceof LoginTokenBean) {
            //获取用户信息
            LoginTokenBean loginToken = (LoginTokenBean) token;
            this.userCode = loginToken.getUsername();
            this.bankNo = loginToken.getBankNo();
            //获取用户密码错误次数
            retryCount = (AtomicInteger) redisUtil.get(getLockKey(userCode, bankNo));
            if (retryCount == null) {
                //如果用户没有登陆过,登陆次数加1 并放入缓存
                retryCount = new AtomicInteger(0);
            }
            //超过最大次数
            long errorLimit = userProperties.getErrorLimit();
            if (retryCount.incrementAndGet() > errorLimit) {
                //锁定用户
                if (userService.updateStatus(userCode, bankNo, UserStatusEnum.STATUS_LOCKED)) {
                    //设置自动解锁时间，登录时触发解锁
                    long lockTimeout = shiroProperties.getLockTimeout();
                    redisUtil.set(getLockKey(userCode, bankNo), errorLimit, lockTimeout, TimeUnit.MINUTES);
                    log.info("超过最大重试次数[{}]，用户[{}]已被锁定，锁定时间[{}]分钟！", errorLimit, userCode, lockTimeout);
                    //抛出用户锁定异常
                    throw new LockedAccountException();
                }
            }
        }
        //加密
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName(ShiroConstant.HASH_ALGORITHM_NAME);
        hashedCredentialsMatcher.setHashIterations(ShiroConstant.HASH_ITERATORS);
        //是否存储为16进制
        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
        //判断用户账号和密码是否正确
        boolean matches = hashedCredentialsMatcher.doCredentialsMatch(token, info);
        if (matches) {
            //如果正确, 从缓存中将用户登录计数清除
            redisUtil.del(getLockKey(userCode, bankNo));
        }else{
            //失败+1，24小时后强制失效
            redisUtil.set(getLockKey(userCode, bankNo), retryCount, 1, TimeUnit.DAYS);
        }
        return matches;
    }
}