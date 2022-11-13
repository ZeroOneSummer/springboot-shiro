package com.bocsoft.obss.shiro.shiro;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bocsoft.obss.shiro.entity.UserBean;
import com.bocsoft.obss.shiro.mapper.UserMapper;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户领域
 * 加入到spring容器后就能在SecurityManager形参内自动注入
 */
@Component
public class UserRealm extends AuthorizingRealm {

    @Autowired
    UserMapper userMapper;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String username = (String) principalCollection.getPrimaryPrincipal();
        //查询DB
        LambdaQueryWrapper<UserBean> query = Wrappers.lambdaQuery();
        query.eq(UserBean::getUsername, username);
        UserBean user = userMapper.selectOne(query);
        if (user == null) {
            throw new UnknownAccountException();
        }
        String roles = user.getRoles();
        String perms = user.getPerms();
        Set<String> rolesSet = new HashSet<>();
        Set<String> permsSet = new HashSet<>();
        if (StringUtils.hasText(roles)){
            rolesSet = new HashSet<>(Arrays.asList(roles.split(",")));
        }
        if (StringUtils.hasText(perms)){
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
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        //获取用户在浏览器中输入的账号
        String username = token.getUsername();
        //查询db
        LambdaQueryWrapper<UserBean> query = Wrappers.lambdaQuery();
        query.eq(UserBean::getUsername, username);
        UserBean user = userMapper.selectOne(query);
        if (user == null) {
            //没有返回登录用户名, 自动抛出UnknownAccountException异常
            return null;
        }
        return new SimpleAccount(username, user.getPassword(), ByteSource.Util.bytes(user.getSalt()), this.getName());
    }

    /**
     * 重写方法,清除当前用户的的 授权缓存
     */
    @Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    /**
     * 重写方法，清除当前用户的 认证缓存
     */
    @Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

    /**
     * 自定义方法：清除所有 授权缓存
     */
    public void clearAllCachedAuthorizationInfo() {
        getAuthorizationCache().clear();
    }

    /**
     * 自定义方法：清除所有 认证缓存
     */
    public void clearAllCachedAuthenticationInfo() {
        getAuthenticationCache().clear();
    }

    /**
     * 自定义方法：清除所有的  认证缓存  和 授权缓存
     */
    public void clearAllCache() {
        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();
    }
}
