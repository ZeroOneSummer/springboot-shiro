package com.bocsoft.obss.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    // swagger文档的配置
    @Bean
    public Docket createDocket(){
        Docket docket = new Docket(DocumentationType.SWAGGER_2)  // 类型
                .apiInfo(apiInfo())                     // 描述
                .select()                               // 扫描
                .apis(RequestHandlerSelectors.basePackage("com.bocsoft.obss.shiro.controller")) // 指定扫描的包
                .paths(PathSelectors.any())             // 扫描范围是any，即扫描所有
                .build()
                .enable(true);                          // 根据配置文件中的值来判断是否启用swagger
        Parameter parameter = new ParameterBuilder()
                .parameterType("header")  //类型为请求头
                .name("Token")  //属性名
                .description("认证token")
                .modelRef(new ModelRef("string"))  //属性类型
                .required(true) //是否必填
                .build();
        //全局参数设置
        docket.globalOperationParameters(Collections.singletonList(parameter));
        return docket;
    }

    // 配置api文档的描述
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("Zero's Swagger接口文档")
                .description("Shiro研究")
                .termsOfServiceUrl("http://www.zero.com") // 扫描的Url
                .version("2.0")
                .build();
    }
}
