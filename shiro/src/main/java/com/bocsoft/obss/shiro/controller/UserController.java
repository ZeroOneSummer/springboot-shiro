package com.bocsoft.obss.shiro.controller;

import com.bocsoft.obss.common.bean.Result;
import com.bocsoft.obss.common.enums.PwdStatusEnum;
import com.bocsoft.obss.common.shiro.constant.ShiroConstant;
import com.bocsoft.obss.common.util.ShiroUtil;
import com.bocsoft.obss.shiro.entity.LoginTokenBean;
import com.bocsoft.obss.shiro.entity.UserBean;
import com.bocsoft.obss.shiro.entity.UserVo;
import com.bocsoft.obss.shiro.service.UserService;
import com.bocsoft.obss.shiro.shiro.UserRealm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Api(tags = "用户模块")
@Controller
@RequestMapping("user")
public class UserController {

    @Autowired
    UserRealm userRealm;

    @Autowired
    UserService userService;

    @ApiIgnore
    @GetMapping("login.html")
    public String login() {
        //进入登录页面
        return "login";
    }

    @ApiIgnore
    @GetMapping("register.html")
    public String register() {
        return "register";
    }

    /**
     * 提交登录
     *
     * @param userCode
     * @param passWord
     * @param rememberMe
     * @return
     */
    @ApiOperation("登录")
    @PostMapping("login")
    public @ResponseBody
    Result<UserVo> login(
            @ApiParam(name = "userCode", value = "用户编号", defaultValue = "BOC0001")
            @RequestParam(value = "userCode") String userCode,
            @ApiParam(name = "passWord", value = "密码", defaultValue = "123456")
            @RequestParam(value = "passWord") String passWord,
            @ApiParam(name = "bankNo", value = "银行号", defaultValue = "105")
            @RequestParam(value = "bankNo") String bankNo,
            @RequestParam(value = "rememberMe", required = false) Boolean rememberMe) {
        //创建一个shiro的Subject对象token，利用这个对象来完成用户的登录认证
        Subject subject = SecurityUtils.getSubject();
        //防止重复登录
        subject.logout();
        //创建一个用户账号和密码的Token对象，并设置用户输入的账号和密码
        LoginTokenBean token = new LoginTokenBean(userCode, passWord, bankNo);
        //记住我
        if (rememberMe != null && rememberMe) {
            token.setRememberMe(rememberMe);
        }
        //判断当前用户是否已经认证过，如果已经认证过着不需要认证;如果没有认证过则完成认证
        if (!subject.isAuthenticated()) {
            try {
                //调用login后，Shiro就会自动执行自定义的Realm中的认证方法
                subject.login(token);
                log.info("登录成功！");
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
        //登录校验
        PwdStatusEnum pwdStatusEnum = userService.checkLogin(userCode, bankNo);
        //获取sessionId作为token返回给前端(需关闭向页面发生cookie)
        String webToken = subject.getSession().getId().toString();
        return Result.success(UserVo.builder().token(webToken)
                .userCode(userCode).bankNo(bankNo).pwdStatus(pwdStatusEnum.getCode()).build());
    }

    /**
     * 修改密码
     *
     * @param userCode
     * @param oldPassWord
     * @param newPassWord
     * @return
     */
    @ApiOperation("修改密码")
    @PostMapping("modifPassword")
    public @ResponseBody
    Result<UserVo> modifPassword(@RequestParam(value = "userCode") String userCode,
                                 @RequestParam(value = "oldPassWord") String oldPassWord,
                                 @RequestParam(value = "newPassWord") String newPassWord) {
        Assert.hasText(userCode, "userCode|用户名不能为空！");
        Assert.hasText(oldPassWord, "oldPassWord|原密码不能为空！");
        Assert.hasText(newPassWord, "newPassWord|新密码不能为空！");
        Assert.isTrue((newPassWord.length() >= 8 && newPassWord.length() <= 16), "newPassWord|新密码长度只能在8-16位长度！");
        Assert.isTrue(newPassWord.equals(oldPassWord), "newPassWord|新密码不能和原密码一样！");
        //创建一个shiro的Subject对象token，利用这个对象来完成用户的登录认证
        Subject subject = SecurityUtils.getSubject();
        String currentUserCode = (String) subject.getPrincipal();
        Assert.isTrue(userCode.equals(currentUserCode), "userCode|不是当前登录用户，无法修改！");
        //查询用户
        String bankNo = (String) userRealm.getSession().getAttribute(UserRealm.LOGIN_BANK_NO);
        Assert.hasText(bankNo, "bankNo|银行号不能为空！");
        UserBean userBean = userService.selectOne(userCode, bankNo);
        if (userBean == null){
            throw new UnknownAccountException();
        }
        //加密比较
        String hexOldPassword = new SimpleHash(ShiroConstant.HASH_ALGORITHM_NAME,
                oldPassWord, userBean.getSalt(), ShiroConstant.HASH_ITERATORS).toString();
        Assert.isTrue(hexOldPassword.equals(userBean.getPassWord()), "oldPassWord|原密码错误！");
        //密码校验
        userService.checkModifPwd(userCode, bankNo, newPassWord);
        //加密新密码
        String salt = ShiroUtil.getSalt(ShiroConstant.SALT_LENGTH);
        String hexNewPassword = new SimpleHash(ShiroConstant.HASH_ALGORITHM_NAME,
                newPassWord, salt, ShiroConstant.HASH_ITERATORS).toString();
        //修改
        UserBean update = UserBean.builder()
                .userCode(userCode)
                .bankNo(bankNo)
                .passWord(hexNewPassword)
                .build();
        boolean rs = userService.modifPassword(update);
        return rs ? Result.success() : Result.error("密码修改失败！");
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
    public String register(@Valid @RequestBody UserBean userBean) {
        //加密
        String salt = ShiroUtil.getSalt(ShiroConstant.SALT_LENGTH);
        String hexPassword = new SimpleHash(ShiroConstant.HASH_ALGORITHM_NAME,
                userBean.getPassWord(), salt, ShiroConstant.HASH_ITERATORS).toString();
        //入库
        UserBean insert = UserBean.builder()
                .userCode(userBean.getUserCode())
                .passWord(hexPassword)
                .salt(salt)
                .bankNo(userBean.getBankNo())
                .roles("visitor")
                .perms("query")
                .build();
        return userService.insert(insert)
                ? "redirect:/user/login.html"
                : "redirect:/user/register.html";
    }

    @ApiOperation(value = "登出", notes = "登出")
    @PostMapping("logout")
    public @ResponseBody
    Result<String> logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return Result.success();
    }

    /**
     * 获取用户权限列表
     *
     * @return
     */
    @ApiOperation("用户权限")
    @PostMapping("getAuthorList")
    public @ResponseBody
    Result<List<String>> getAuthorList() {
        Subject subject = SecurityUtils.getSubject();
        List<String> authors = new ArrayList<>(userRealm.authorInfos(subject.getPrincipals()));
        return Result.success(authors);
    }

    /**
     * 清除用户缓存
     *
     * @return
     */
    @ApiOperation("清除缓存")
    @PostMapping("clearCache")
    public @ResponseBody
    Result<List<String>> clearCache() {
        userRealm.clearCache();
        return Result.success();
    }

    /**
     * 解锁用户
     *
     * @param userCode
     * @param bankNo
     * @return
     */
    @ApiOperation("解锁用户")
    @PostMapping("unlockAccount")
    public @ResponseBody
    Result<String> unlockAccount(
            @ApiParam(name = "userCode", value = "用户编号")
            @RequestParam(value = "userCode") String userCode,
            @ApiParam(name = "bankNo", value = "银行号")
            @RequestParam(value = "bankNo") String bankNo) {

        Assert.hasText(userCode, "userCode|用户名不能为空！");
        Assert.hasText(bankNo, "bankNo|银行号不能为空！");
        boolean rt = userService.unlockAccount(userCode, bankNo);
        return rt ? Result.success() : Result.error("解锁失败！");
    }

    @RequiresRoles(value = {"admin", "teller"}, logical = Logical.OR)
    @ApiOperation(value = "test", notes = "权限测试-角色")
    @PostMapping("test")
    public @ResponseBody
    Result<String> test() {
        log.info("权限测试-角色, ok!");
        return Result.success("test ok");
    }

    @RequiresPermissions("delete")
    @ApiOperation(value = "删除", notes = "权限测试-del")
    @PostMapping("del")
    public @ResponseBody
    Result<String> del() {
        log.info("权限测试-del, ok!");
        return Result.success("del ok");
    }
}
