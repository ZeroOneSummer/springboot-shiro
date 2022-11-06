package com.bocsoft.obss.one.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
//@Component
@ConfigurationProperties(prefix = "zero")
public class ConsulConfig {
    private String name;
    private Integer age;
}
