package com.bocsoft.obss.common.shiro.config.filter;

import com.bocsoft.obss.common.shiro.config.web.ShiroConfig;
import com.bocsoft.obss.common.shiro.config.web.ShiroProperties;
import com.bocsoft.obss.common.shiro.session.ShiroSessionManager;
import com.bocsoft.obss.common.util.RedisUtil;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.AbstractShiroWebFilterConfiguration;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.Map;

@Configuration
@ConditionalOnProperty(name = {"shiro.enabled"}, matchIfMissing = true)
@AutoConfigureAfter({ShiroConfig.class})
public class ShiroWebFilterConfiguration extends AbstractShiroWebFilterConfiguration {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    ShiroSessionManager shiroSessionManager;

    @Autowired
    ShiroProperties shiroProperties;

    @Bean(name = {"filterShiroFilterRegistrationBean"})
    @ConditionalOnMissingBean
    protected FilterRegistrationBean filterShiroFilterRegistrationBean() throws Exception {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, new DispatcherType[]{DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ERROR});
        filterRegistrationBean.setFilter((AbstractShiroFilter)this.shiroFilterFactoryBean().getObject());
        //自定义filter
        filterRegistrationBean.setName("shiroFilterFactoryBean");
        filterRegistrationBean.setOrder(1);
        return filterRegistrationBean;
    }

    @Bean
    @ConditionalOnMissingBean
    @Override
    protected ShiroFilterFactoryBean shiroFilterFactoryBean() {
        ShiroFilterFactoryBean filterFactoryBean = super.shiroFilterFactoryBean();
        Map<String, Filter> filters = filterFactoryBean.getFilters();
        //自定义filter
        filters.put("authc", new ShiroAuthenFilter());
        filters.put("kickout", new KickoutSessionControlFilter(shiroSessionManager, redisUtil, shiroProperties));
        return filterFactoryBean;
    }
}
