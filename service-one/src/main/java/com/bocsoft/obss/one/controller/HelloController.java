package com.bocsoft.obss.one.controller;

import com.bocsoft.obss.one.config.ConsulConfig;
import com.bocsoft.obss.one.service.HelloService;
import com.bocsoft.obss.one.service.ProxyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/s1/") // http://localhost:8081/s1/hello/lisa
public class HelloController {

    @Autowired
    HelloService helloService;

    @Autowired
    ProxyService proxyService;

    @Value("${zero.info:none}")
    String info;

    //配置中心取值
    @Autowired
    ConsulConfig config;

    @GetMapping("hello/{msg}")
    public String toHello(@PathVariable("msg") String msg){
        log.info("配置中心-@ConfigurationProperties取值: {}", config.toString());
        log.info("配置中心-@Value取值: {}", info);
        return helloService.toHello("你好，s2! 信息：" + msg);
    }

    @GetMapping("/proxy/hello/{msg}")
    public String proxyToHello(@PathVariable("msg") String msg){
        return proxyService.toHello("你好，s2! 代理信息：" + msg);
    }
}
