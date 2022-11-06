package com.bocsoft.obss.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;

@Component
@Slf4j
public class PostFirstFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return POST_TYPE;
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

        log.info("经过第一个 post 过滤器");

        RequestContext ctx = RequestContext.getCurrentContext();
        String responseBody = ctx.getResponseBody();
        if (StringUtils.isNotBlank(responseBody)) {
            // 说明逻辑没有通过
            // 设置编码
            ctx.getResponse().setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            ctx.getResponse().setCharacterEncoding("UTF-8");
            // 更改响应状态
            ctx.setResponseStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            // 替换掉响应报文
            ctx.setResponseBody(responseBody);
        }
        return null;
    }

}