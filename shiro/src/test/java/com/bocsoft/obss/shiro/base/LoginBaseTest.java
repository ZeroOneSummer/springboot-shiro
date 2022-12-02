package com.bocsoft.obss.shiro.base;

import lombok.SneakyThrows;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.subject.WebSubject;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


/**
 * 登录基类，shiro模块用
 */
public class LoginBaseTest extends BaseTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private SecurityManager securityManager;
    private Subject subject;

    MockMvc mockMvc;
    MockHttpSession mockHttpSession;
    MockHttpServletRequest mockHttpServletRequest;
    MockHttpServletResponse mockHttpServletResponse;

    @Before
    public void setUp() {
        initUser();
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                //.addFilter(new DelegatingFilterProxy("shiroFilter", wac), "/*")
                .build();
        mockHttpServletRequest = new MockHttpServletRequest(wac.getServletContext());
        mockHttpServletResponse = new MockHttpServletResponse();
        mockHttpSession = new MockHttpSession(wac.getServletContext());
        mockHttpServletRequest.setSession(mockHttpSession);
        SecurityUtils.setSecurityManager(securityManager);
        subject = new WebSubject.Builder(mockHttpServletRequest, mockHttpServletResponse).buildWebSubject();
        UsernamePasswordToken passwordToken = new UsernamePasswordToken(USERNAME, PASSWORD);
        subject.login(passwordToken);
        ThreadContext.bind(subject);
    }

    @SneakyThrows
    protected String reqWebController(MockHttpServletRequestBuilder mock) {
        return mockMvc.perform(mock)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()
                ;
    }
}
