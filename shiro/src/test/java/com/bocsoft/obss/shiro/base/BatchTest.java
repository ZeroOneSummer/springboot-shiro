package com.bocsoft.obss.shiro.base;

import com.bocsoft.obss.shiro.H2Test;
import com.bocsoft.obss.shiro.RedisTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * 批量执行测试用例
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    H2Test.class,
    RedisTest.class
})
public class BatchTest {
}
