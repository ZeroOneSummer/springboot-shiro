package com.bocsoft.obss.zuul;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

@RefreshScope //@Value需要此注解进行配置刷新
@Slf4j
@EnableDiscoveryClient  //可省略不写
@EnableZuulProxy
@SpringBootApplication
public class ZuulApplication implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${server.port}")
    Integer port;

    public static void main(String[] args) {
        SpringApplication.run(ZuulApplication.class, args);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("=> ZuulApplication 启动成功! port: {}", port);
    }
}
