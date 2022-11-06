package com.bocsoft.obss.two;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

@Slf4j
@EnableDiscoveryClient  //可省略不写
@SpringBootApplication
public class TwoApplication implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${server.port}")
    Integer port;

    public static void main(String[] args) {
        SpringApplication.run(TwoApplication.class, args);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("=> TwoApplication 启动成功! port: {}", port);
    }
}
