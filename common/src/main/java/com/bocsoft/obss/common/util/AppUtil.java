package com.bocsoft.obss.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 获取spring上下文工具类
 */
@Deprecated
public class AppUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext == null) {
            AppUtil.context = applicationContext;
        }
    }

    public static ApplicationContext getApplicationContext() {
        return AppUtil.context;
    }

    /**
     * 根据工厂中的类名获取类实例
     */
    public static <T> T getBean(Class<T> tClass){
        return getApplicationContext().getBean(tClass);
    }

    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }
}