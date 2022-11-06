package com.bocsoft.obss.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

@Component
@Slf4j
public class FirstPreFilter extends ZuulFilter {

    final String LOGIC_IS_SUCCESS = "LOGIC_IS_SUCCESS";
    final String ERROR_RESPONSE_BODY = "{\"status\": 10600, \"msg\":\"%s\"}";

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        log.info("经过第一个 pre 过滤器");
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        if (StringUtils.isBlank(request.getHeader("a"))) {
            // 未经过逻辑
            // 用来给后面的 Filter 标识，是否继续执行
            ctx.set(LOGIC_IS_SUCCESS, false);
            // 返回信息
            ctx.setResponseBody(String.format(ERROR_RESPONSE_BODY, "a Header头不足"));
            // 对该请求禁止路由，禁止访问下游服务
            ctx.setSendZuulResponse(false);
            return null;
        }

        // 用来给后面的 Filter 标识，是否继续执行
        ctx.set(LOGIC_IS_SUCCESS, true);
        return null;
    }

}