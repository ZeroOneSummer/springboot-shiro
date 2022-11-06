package com.bocsoft.obss.shiro;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

@Slf4j
@MapperScan("com.bocsoft.obss.shiro.mapper")
@SpringBootApplication
public class App implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${server.port}")
    Integer port;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("=> ShiroApp 启动成功! port: {}", port);
    }
}
