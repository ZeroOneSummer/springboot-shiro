package com.bocsoft.obss.common.shiro.config.filter;

import com.bocsoft.obss.common.bean.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.http.MediaType;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 自定义过滤
 */
@Slf4j
public class ShiroAuthenFilter extends FormAuthenticationFilter {

    /**
     * 是否是拒绝登录
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        //是否为LoginUrl
        if (this.isLoginRequest(request, response)) {
            //是否post提交登录
            if (this.isLoginSubmission(request, response)) {
                if (log.isTraceEnabled()) {
                    log.trace("Login submission detected.  Attempting to execute login.");
                }
                //执行登录
                return this.executeLogin(request, response);
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("Login page view.");
                }
                //进入登录页面
                return true;
            }
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Attempting to access a path which requires authentication.  Forwarding to the Authentication url [" + this.getLoginUrl() + "]");
            }
            //自定义返回错误格式
            Result<String> error = Result.error("用户名或密码错误！");
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            PrintWriter writer = response.getWriter();
            writer.write(new ObjectMapper().writeValueAsString(error));
            writer.flush();
            writer.close();
            return false;
        }
    }
}
