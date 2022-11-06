package com.bocsoft.obss.shiro.shiro;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class AppUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return AppUtil.context;
    }

    /**
     * 根据工厂中的类名获取类实例
     */
    public static <T> T getBean(Class<T> tClass){
        return (T)getApplicationContext().getBean(tClass.getSimpleName());
    }
}