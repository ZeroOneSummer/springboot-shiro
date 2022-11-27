package com.bocsoft.obss.shiro.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.github.jeffreyning.mybatisplus.conf.EnableMPP;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableMPP //启用MMP注解
@Configuration
public class MyBatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //乐观锁（配合字段上@Version使用）
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        //指定数据库
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
