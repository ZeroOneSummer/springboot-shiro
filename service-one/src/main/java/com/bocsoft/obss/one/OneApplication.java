package com.bocsoft.obss.one;

import com.bocsoft.obss.one.config.ConsulConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

@Slf4j
@EnableFeignClients
@EnableDiscoveryClient  //可省略不写
@SpringBootApplication
@EnableConfigurationProperties(value = {ConsulConfig.class}) //可省略@Component
public class OneApplication implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${server.port}")
    Integer port;

    public static void main(String[] args) {
        SpringApplication.run(OneApplication.class, args);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("=> OneApplication 启动成功! port: {}", port);
    }
}
