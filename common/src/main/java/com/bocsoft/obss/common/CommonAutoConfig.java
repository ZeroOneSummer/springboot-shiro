package com.bocsoft.obss.common;

import com.bocsoft.obss.common.config.SwaggerConfig;
import com.bocsoft.obss.common.redis.RedisConfig;
import com.bocsoft.obss.common.shiro.config.web.ShiroAnnotationConfig;
import com.bocsoft.obss.common.shiro.config.web.ShiroConfig;
import com.bocsoft.obss.common.shiro.config.web.ShiroWebAutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * 公共配置入口
 */
@Import({
    SwaggerConfig.class,
    RedisConfig.class,
    ShiroConfig.class,
    ShiroWebAutoConfiguration.class,
    ShiroAnnotationConfig.class
})
public class CommonAutoConfig {
}
