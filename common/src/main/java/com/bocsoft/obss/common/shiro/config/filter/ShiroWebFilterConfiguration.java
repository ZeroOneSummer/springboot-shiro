package com.bocsoft.obss.common.shiro.config.filter;

import com.bocsoft.obss.common.shiro.config.web.ShiroConfig;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.AbstractShiroWebFilterConfiguration;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;

@Configuration
@ConditionalOnProperty(name = {"shiro.enabled"}, matchIfMissing = true)
@AutoConfigureAfter({ShiroConfig.class})
public class ShiroWebFilterConfiguration extends AbstractShiroWebFilterConfiguration {

    /**
     * 自定义filter
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @Override
    protected ShiroFilterFactoryBean shiroFilterFactoryBean() {
        ShiroFilterFactoryBean shiroFilterFactoryBean = super.shiroFilterFactoryBean();
        shiroFilterFactoryBean.getFilters().put("authc", new ShiroAuthenFilter());
        return shiroFilterFactoryBean;
    }

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
}
