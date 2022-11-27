package com.bocsoft.obss.shiro;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@EnableAsync  //开启异步，否则 @Async 不生效
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
        log.info("=> {} 启动成功! port: {}", this.getClass().getSimpleName().split("\\$")[0], port);
    }
}
