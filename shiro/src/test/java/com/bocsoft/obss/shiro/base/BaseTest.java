package com.bocsoft.obss.shiro.base;

import com.bocsoft.obss.shiro.App;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class BaseTest {
    protected final static ObjectMapper JSON = new ObjectMapper();
}
