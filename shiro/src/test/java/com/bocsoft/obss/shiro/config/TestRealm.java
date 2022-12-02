package com.bocsoft.obss.shiro.config;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestRealm extends AuthorizingRealm {

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String userName = principalCollection.getPrimaryPrincipal().toString();
        //查询DB
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(Stream.of("admin, teller, visit").collect(Collectors.toSet()));
        info.setStringPermissions(Stream.of("query, update, delete, add").collect(Collectors.toSet()));
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        if (authenticationToken instanceof UsernamePasswordToken){
            UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
            return new SimpleAccount(token.getUsername(), token.getPassword(), this.getName());
        }
        return null;
    }
}
