package com.bocsoft.obss.common.shiro.session;

import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

/**
 * 【从请求头获取token】
 */
public class ShiroSessionManager extends DefaultWebSessionManager {

    public static final String TOKEN = "Token";
    public static final String USERNAME = "userCode";

    /**
     * 从请求头获取token或直接从redis里获取
     * @param request
     * @param response
     * @return
     */
    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        String token = WebUtils.toHttp(request).getHeader(TOKEN);
        if (StringUtils.hasText(token)){
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, token);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, "Stateless request");
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, true);
            //如果找不到该token，进入ShiroAuthenFilter决定是否放行
            return token;
        } else {
            return super.getSessionId(request, response);
        }
    }
}
