package com.bocsoft.obss.zuul.filter;

import com.bocsoft.obss.common.shiro.session.RedisSessionDAO;
import com.bocsoft.obss.common.shiro.session.ShiroSessionManager;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SimpleSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

/**
 * 白名单过滤器
 */
@Slf4j
public class WhiteFilter extends ZuulFilter {

    @Autowired
    RedisSessionDAO redisSessionDAO;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 白名单
     *
     * @return
     */
    @Override
    public boolean shouldFilter() {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        String uri = request.getRequestURI();
        boolean inWhiles = true; //uri是否在白名单中，待客户化
        return inWhiles;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String token = request.getHeader(ShiroSessionManager.TOKEN);
        if (StringUtils.hasText(token)) {
            try {
                Session session = redisSessionDAO.readSession(token);
                if (session instanceof SimpleSession) {
                    if (!(((SimpleSession) session).isValid())) {
                        this.stopForward(requestContext);
                        return null;
                    }
                }
                //刷新
                session.touch();
                redisSessionDAO.update(session);
                return null;
            } catch (UnknownSessionException e) {
                log.error("Not found session id[{}]", token);
            }
        }
        this.stopForward(requestContext);
        return null;
    }

    private void stopForward(RequestContext requestContext) {
        requestContext.setSendZuulResponse(false);
        requestContext.getResponse().setCharacterEncoding(StandardCharsets.UTF_8.toString());
    }
}
