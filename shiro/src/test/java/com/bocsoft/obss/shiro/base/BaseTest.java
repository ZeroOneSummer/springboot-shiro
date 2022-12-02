package com.bocsoft.obss.shiro.base;

import com.bocsoft.obss.shiro.App;
import com.bocsoft.obss.shiro.config.ShiroConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

//@FixMethodOrder(MethodSorters.NAME_ASCENDING) //按方法首字母排序执行单元测试
@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
//@Import(ShiroConfig.class) //生效
public class BaseTest {
    protected final static ObjectMapper JSON = new ObjectMapper();
    protected final static String USERNAME = "lisa";
    protected final static String PASSWORD = "123456";

    public void initUser() {
        //添加用户
    }
}
