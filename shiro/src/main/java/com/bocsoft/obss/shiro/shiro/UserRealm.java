package com.bocsoft.obss.shiro.shiro;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.bocsoft.obss.common.enums.UserStatusEnum;
import com.bocsoft.obss.common.util.RedisUtil;
import com.bocsoft.obss.shiro.entity.LoginTokenBean;
import com.bocsoft.obss.shiro.entity.UserBean;
import com.bocsoft.obss.shiro.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用户领域
 * 加入到spring容器后就能在SecurityManager形参内自动注入
 *
 * @Component 必须加上，不然需要显式的注入
 */
@Slf4j
@Component
public class UserRealm extends AuthorizingRealm {
    public static final String LOGIN_BANK_NO = "shiro:login:bankno";

    @Autowired
    UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String userCode = (String) principalCollection.getPrimaryPrincipal();
        String bankNo = (String) getSession().getAttribute(LOGIN_BANK_NO);
        //查询DB
        UserBean user = userService.selectOne(userCode, bankNo);
        if (user == null) {
            log.error("用户{}不存在！", userCode);
            throw new UnknownAccountException();
        }
        String roles = user.getRoles();
        String perms = user.getPerms();
        Set<String> rolesSet = new HashSet<>();
        Set<String> permsSet = new HashSet<>();
        if (StringUtils.hasText(roles)) {
            rolesSet = new HashSet<>(Arrays.asList(roles.split(",")));
        }
        if (StringUtils.hasText(perms)) {
            permsSet = new HashSet<>(Arrays.asList(perms.split(",")));
        }
        //赋值
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(rolesSet);
        info.setStringPermissions(permsSet);
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //将AuthenticationToken强转成UsernamePasswordToken 这样获取账号和密码更加的方便
        LoginTokenBean loginToken = (LoginTokenBean) authenticationToken;
        //获取用户在浏览器中输入的账号
        String userCode = loginToken.getUsername();
        String bankNo = loginToken.getBankNo();
        //鉴权时取出
        getSession().setAttribute(LOGIN_BANK_NO, bankNo);
        //查询db
        UserBean user = userService.selectOne(userCode, bankNo);
        if (user == null) {
            log.error("用户不存在!");
            return null;
        }
        //是否锁定
        if (UserStatusEnum.STATUS_LOCKED.getCode().equals(user.getStatus())){
            //是否需要触发自动解锁用户
            AtomicInteger retryCount = (AtomicInteger) redisUtil.get(LoginCredentialsMatcher.getLockKey(userCode, bankNo));
            //已到解锁时间
            if (retryCount == null){
                userService.unlockAccount(userCode, bankNo);
            }else{
                log.error("用户{}已被锁定！", userCode);
                throw new LockedAccountException();
            }
        }
        return new SimpleAccount(userCode, user.getPassWord(), ByteSource.Util.bytes(user.getSalt()), this.getName());
    }

    /**
     * shiro-thymeleaf方言
     */
    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }

    /**
     * 清除缓存
     */
    public void clearCache() {
        this.getAuthorizationCache().clear();
        this.getAuthenticationCache().clear();
    }

    /**
     * 获取权限
     */
    public Collection<String> authorInfos(PrincipalCollection principalCollection) {
        return this.doGetAuthorizationInfo(principalCollection).getStringPermissions();
    }

    /**
     * 获取session
     * @return
     */
    public HttpSession getSession() {
        //SecurityUtils.getSubject().getSession() 和下面是同一个session
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.currentRequestAttributes())).getRequest();
        return request.getSession();
    }

    /**
     * 密码比较器
     */
    @Autowired
    @Qualifier("loginCredentialsMatcher")
    @Override
    public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
        //加密比较逻辑移到LoginCredentialsMatcher
        super.setCredentialsMatcher(credentialsMatcher);
    }
}
