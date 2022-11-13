package com.bocsoft.obss.common;

import com.bocsoft.obss.common.redis.RedisConfig;
import com.bocsoft.obss.common.shiro.config.ShiroAnnotationConfig;
import com.bocsoft.obss.common.shiro.config.ShiroConfig;
import com.bocsoft.obss.common.shiro.config.ShiroWebConfig;
import org.springframework.context.annotation.Import;

/**
 * 公共配置入口
 */
@Import({ShiroConfig.class, RedisConfig.class, ShiroWebConfig.class, ShiroAnnotationConfig.class})
//@Import({RedisConfig.class})
public class CommonAutoConfig {
}
