package com.bocsoft.obss.shiro;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bocsoft.obss.shiro.base.BaseTest;
import com.bocsoft.obss.shiro.entity.UserBean;
import com.bocsoft.obss.shiro.mapper.UserMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@Slf4j
public class H2Test extends BaseTest {

    @Autowired
    UserMapper userMapper;

    /**
     * test里开启事务，默认是回滚的
     */
    @Rollback(false)
    @SneakyThrows
    @Test
    public void addH2() {
        UserBean user = UserBean.builder().username("lisa").password("123456").build();
        int rt = userMapper.insert(user);
        Assert.assertEquals("插入失败!", 1, rt);
        LambdaQueryWrapper<UserBean> wrapper = Wrappers.lambdaQuery();
        List<UserBean> list = userMapper.selectList(wrapper);
        JSON.writeValueAsString(list);
    }
}
