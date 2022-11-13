package com.bocsoft.obss.shiro.controller;

import com.bocsoft.obss.common.bean.Result;
import com.bocsoft.obss.shiro.entity.LoginTokenBean;
import com.bocsoft.obss.shiro.entity.UserBean;
import com.bocsoft.obss.shiro.entity.UserVo;
import com.bocsoft.obss.shiro.mapper.UserMapper;
import com.bocsoft.obss.common.shiro.constant.ShiroConstant;
import com.bocsoft.obss.common.util.ShiroUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@Api(value = "用户模块", description = "用户模块")
@Controller
@RequestMapping("user")
public class UserController {

    @Autowired
    UserMapper userMapper;

    @ApiIgnore
    @GetMapping("login.html")
    public String login(){
        //进入登录页面
        return "login";
    }

    @ApiIgnore
    @GetMapping("register.html")
    public String register(){
        return "register";
    }

    /**
     * 提交登录
     * @param username
     * @param password
     * @param rememberme
     * @return
     */
    @ApiOperation(value = "登录", notes = "登录")
    @PostMapping("login")
    public @ResponseBody Result<UserVo> login(
                @ApiParam(name = "username", value = "用户名", defaultValue = "BOC0001")
                @RequestParam(value = "username") String username,
                @ApiParam(name = "password", value = "密码", defaultValue = "123456")
                @RequestParam(value = "password") String password,
                @ApiParam(name = "bankno", value = "银行号", defaultValue = "105")
                @RequestParam(value = "bankno") String bankno,
                @RequestParam(value = "rememberme", required = false) Boolean rememberme) {

        //创建一个shiro的Subject对象token，利用这个对象来完成用户的登录认证
        Subject subject = SecurityUtils.getSubject();
        //防止重复登录
        subject.logout();
        //创建一个用户账号和密码的Token对象，并设置用户输入的账号和密码
        LoginTokenBean token = new LoginTokenBean(username, password, bankno);
        //记住我
        if (rememberme != null && rememberme) {
            token.setRememberMe(rememberme);
        }
        //判断当前用户是否已经认证过，如果已经认证过着不需要认证;如果没有认证过则完成认证
        if (!subject.isAuthenticated()) {
            try {
                //调用login后，Shiro就会自动执行自定义的Realm中的认证方法
                subject.login(token);
            } catch (UnknownAccountException e) {
                //表示用户的账号错误，这个异常是在后台抛出
                log.error("账号错误!");
                return Result.error("账号错误!");
            } catch (LockedAccountException e) {
                //表示用户的账号被锁定，这个异常是在后台抛出
                log.error("账号被冻结!");
                return Result.error("账号被冻结!");
            } catch (IncorrectCredentialsException e) {
                //表示用户的密码错误，这个异常是shiro在认证密码时抛出
                log.error("密码错误!");
                return Result.error("密码错误!");
            }
        }
        //获取sessionId作为token返回给前端(需关闭向页面发生cookie)
        String webToken = subject.getSession().getId().toString();
        return Result.success(UserVo.builder().username(username).token(webToken).bankno(bankno).build());
    }

    @ApiOperation(value = "令牌", notes = "用作用户登录后的鉴权令牌")
    @PostMapping("token")
    public String getToken(Model model) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            log.info("未登录！");
            model.addAttribute("msg", "未登录！");
            return "redirect:/user/login.html";
        }
        String webToken = subject.getSession().getId().toString();
        model.addAttribute("token", webToken);
        return "index";
    }

    @ApiOperation(value = "注册", notes = "注册")
    @PostMapping("register")
    public String register(@ApiParam(name = "username", value = "用户名", defaultValue = "lisa")
                           @RequestParam(value = "username") String username,
                           @ApiParam(name = "password", value = "密码", defaultValue = "123456")
                           @RequestParam(value = "password") String password,
                           @ApiParam(name = "bankno", value = "银行号", defaultValue = "105")
                           @RequestParam(value = "bankno") String bankno) {
        String salt = ShiroUtil.getSalt(ShiroConstant.SALT_LENGTH);
        String hexPassword = new SimpleHash(ShiroConstant.HASH_ALGORITHM_NAME, password, salt, ShiroConstant.HASH_ITERATORS).toString();
        int rt = userMapper.insert(UserBean.builder()
                        .username(username)
                        .password(hexPassword)
                        .salt(salt)
                        .bankNo(bankno)
                        .roles("visitor")
                        .perms("query")
                        .build());
        return rt > 0 ? "redirect:/user/login.html" : "redirect:/user//register.html";
    }

    @ApiOperation(value = "登出", notes = "登出")
    @PostMapping("logout")
    public @ResponseBody Result<String> logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return Result.success();
    }

    @RequiresRoles(value = {"admin", "teller"}, logical = Logical.OR)
    @ApiOperation(value = "test", notes = "权限测试-角色")
    @PostMapping("test")
    public @ResponseBody Result<String> test() {
        log.info("权限测试-角色, ok!");
        return Result.success("test ok");
    }

    @RequiresPermissions("delete")
    @ApiOperation(value = "删除", notes = "权限测试-del")
    @PostMapping("del")
    public @ResponseBody Result<String> del() {
        log.info("权限测试-del, ok!");
        return Result.success("del ok");
    }
}
