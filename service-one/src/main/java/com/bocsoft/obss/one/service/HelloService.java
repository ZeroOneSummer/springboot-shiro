package com.bocsoft.obss.one.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "service-two")
public interface HelloService {

    @RequestMapping("/s2/hello/{msg}")
    String toHello(@PathVariable("msg") String msg);
}
