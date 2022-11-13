package com.bocsoft.obss.common.other;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 请求头鉴权拦截器
 */
@Deprecated
@Slf4j
public class FeignAuthenInterceptor /*implements HttpRequestInterceptor*/ {

//    @Override
//    public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
//        HttpServletRequest request = attributes.getRequest();
//        String token = request.getHeader("Token");
//        if (StringUtils.hasText(token)) {
//            httpRequest.setHeader("Token", token);
//        }else {
//            log.error("There are no token headers in current request!");
//        }
//    }
}
