package com.bocsoft.obss.shiro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    // swagger文档的配置
    @Bean
    public Docket createDocket(){

        return new Docket(DocumentationType.SWAGGER_2)  // 类型
                .apiInfo(apiInfo())                     // 描述
                .select()                               // 扫描
                .apis(RequestHandlerSelectors.basePackage("com.bocsoft.obss.shiro.controller")) // 指定扫描的包
                .paths(PathSelectors.any())             // 扫描范围是any，即扫描所有
                .build()
                .enable(true);                          // 根据配置文件中的值来判断是否启用swagger
    }

    // 配置api文档的描述
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("ZERO-SWGGER")
                .description("shiro研究")
                .termsOfServiceUrl("http://www.zero.com") // 扫描的Url
                .version("1.0")
                .build();
    }
}
