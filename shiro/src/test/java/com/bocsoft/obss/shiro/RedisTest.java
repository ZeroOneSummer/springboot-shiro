package com.bocsoft.obss.shiro;

import com.bocsoft.obss.shiro.base.BaseTest;
import com.bocsoft.obss.shiro.entity.UserBean;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.Rollback;

import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisTest extends BaseTest {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Rollback(false)
    @SneakyThrows
    @Test
    public void redisTest() {
        UserBean user = UserBean.builder().username("lisa").password("123456").build();
        redisTemplate.opsForValue().set("zero:obj", user, 30, TimeUnit.SECONDS);
        UserBean obj = (UserBean) redisTemplate.opsForValue().get("zero:obj");
        log.info(JSON.writeValueAsString(obj));
    }
}
