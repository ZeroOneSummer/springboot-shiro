package com.bocsoft.obss.shiro.init;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bocsoft.obss.common.util.RedisUtil;
import com.bocsoft.obss.shiro.entity.UserBean;
import com.bocsoft.obss.shiro.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 初始化
 */
@Slf4j
@Component
public class RedisInitData implements CommandLineRunner {

    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisUtil redisUtill;

    @Value("shiro.redis.init.switch:true")
    boolean initSwitch;

    @Override
    public void run(String... args) throws Exception {
        if (!initSwitch){
            log.info("初始化开关未开启，不进行初始化。");
            return;
        }
        List<UserBean> users = userMapper.selectList(Wrappers.emptyWrapper());
        if (!CollectionUtils.isEmpty(users)){
            Map<String, UserBean> maps = users.stream().collect(Collectors.toMap(
                    (u -> u.getBankNo() + "_" + u.getUserCode()), u -> u));
            redisUtill.hashPutAll("hash:t_user", maps);
            log.info("用户信息Redis铺底完成");
        }
    }
}
