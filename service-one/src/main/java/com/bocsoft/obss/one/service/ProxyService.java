package com.bocsoft.obss.one.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

//网关 + 路由前缀 -> 服务2
@FeignClient(name = "zuul-server", path = "/api/ser2")
public interface ProxyService {

    @RequestMapping("s2/hello/{msg}")
    String toHello(@PathVariable("msg") String msg);
}
