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

    /**
     * 默认回滚事务，@Rollback(true)
     * 回滚事务是为了避免跑单元测试时往DB里写入脏数据
     * 最好不要直接使用DB里的数据，先插一条铺底再操作，避免DB数据清除后流水线跑单元测试报错
     */
    @Rollback(false)
    @SneakyThrows
    @Test
    public void redisTest() {
        UserBean user = UserBean.builder().bankNo("105").userCode("lisa").passWord("123456").build();
        redisTemplate.opsForValue().set("zero:obj", user, 30, TimeUnit.SECONDS);
        UserBean obj = (UserBean) redisTemplate.opsForValue().get("zero:obj");
        log.info(JSON.writeValueAsString(obj));
    }
}
