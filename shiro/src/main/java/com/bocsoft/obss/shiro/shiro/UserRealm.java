package com.bocsoft.obss.shiro.shiro;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bocsoft.obss.common.shiro.constant.ShiroConstant;
import com.bocsoft.obss.shiro.entity.LoginTokenBean;
import com.bocsoft.obss.shiro.entity.UserBean;
import com.bocsoft.obss.shiro.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 用户领域
 * 加入到spring容器后就能在SecurityManager形参内自动注入
 *
 * @Component 必须加上，不然需要显式的注入
 */
@Slf4j
@Component
public class UserRealm extends AuthorizingRealm {
    private static final String LOGIN_BANK_NO = "shiro:login:bankno";

    @Autowired
    UserMapper userMapper;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String username = (String) principalCollection.getPrimaryPrincipal();
        String bankno = (String) getSession().getAttribute(LOGIN_BANK_NO);
        //查询DB
        LambdaQueryWrapper<UserBean> query = Wrappers.lambdaQuery();
        query.eq(UserBean::getUsername, username);
        query.eq(UserBean::getBankNo, bankno);
        UserBean user = userMapper.selectOne(query);
        if (user == null) {
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
        LoginTokenBean token = (LoginTokenBean) authenticationToken;
        //获取用户在浏览器中输入的账号
        String username = token.getUsername();
        String bankno = token.getBankNo();
        //鉴权时取出
        getSession().setAttribute(LOGIN_BANK_NO, bankno);
        //查询db
        LambdaQueryWrapper<UserBean> query = Wrappers.lambdaQuery();
        query.eq(UserBean::getUsername, username);
        query.eq(UserBean::getBankNo, bankno);
        UserBean user = userMapper.selectOne(query);
        if (user == null) {
            //没有返回登录用户名, 自动抛出UnknownAccountException异常
            log.error("用户不存在!");
            return null;
        }
        if (user.getStatus() == 1) {
            log.error("账号已锁定!");
            throw new LockedAccountException();
        }
        return new SimpleAccount(username, user.getPassword(), ByteSource.Util.bytes(user.getSalt()), this.getName());
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
    private HttpSession getSession() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.currentRequestAttributes())).getRequest();
        return request.getSession();
    }

    /**
     * shiro-thymeleaf方言
     */
    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }

    /**
     * 密码比较器
     */
    @Override
    public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName(ShiroConstant.HASH_ALGORITHM_NAME);
        hashedCredentialsMatcher.setHashIterations(ShiroConstant.HASH_ITERATORS);
        //是否存储为16进制
        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
        super.setCredentialsMatcher(hashedCredentialsMatcher);
    }
}
