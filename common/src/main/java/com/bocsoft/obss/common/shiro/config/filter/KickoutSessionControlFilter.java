package com.bocsoft.obss.common.shiro.config.filter;

import cn.hutool.json.JSONUtil;
import com.bocsoft.obss.common.bean.Result;
import com.bocsoft.obss.common.shiro.config.web.ShiroProperties;
import com.bocsoft.obss.common.util.RedisUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Deque;
import java.util.LinkedList;

/**
 * 同一个账号只能一个人使用，否则踢人
 */
@Setter
@Slf4j
public class KickoutSessionControlFilter extends AccessControlFilter {

    //是否被踢标识
    public static final String KICOUTKEY = "kitout_flag";
    //踢出后重定向地址
    private String kickoutUrl = "/user/login.html";
    //是否踢出之后的
    private boolean kickoutAfter = false;
    //同一个帐号最大会话数 默认1
    private int maxSession = 1;

    private String userCode;
    private String bankNo;

    private SessionManager sessionManager;
    private RedisUtil redisUtil;
    private ShiroProperties shiroProperties;;

    public KickoutSessionControlFilter(SessionManager sessionManager, RedisUtil redisUtil, ShiroProperties shiroProperties){
        this.sessionManager = sessionManager;
        this.redisUtil = redisUtil;
        this.shiroProperties = shiroProperties;
    }

    private String getRedisKickoutKey() {
        return shiroProperties.getKickoutPrefix() + this.bankNo + "_" + this.userCode;
    }

    /**
     * 是否允许访问，返回true表示允许
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return false;
    }

    /**
     * 表示访问拒绝时是否自己处理，如果返回true表示自己不处理且继续拦截器链执行，返回false表示自己已经处理了（比如重定向到另一个页面）。
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = this.getSubject(request, response);
        if (!subject.isAuthenticated() && !subject.isRemembered()) {
            //如果没有登录，直接进行之后的流程
            return true;
        }
        //如果有登录，判断是否访问的为静态资源，如果是游客允许访问的静态资源，直接返回true
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String path = httpServletRequest.getServletPath();
        // 如果是静态文件，则返回true
        if (isStaticFile(path)) {
            return true;
        }

        Session session = subject.getSession();
        userCode = (String) subject.getPrincipal();
        bankNo = (String) session.getAttribute("shiro:login:bankno"); //key取user模块的UserRealm.LOGIN_BANK_NO
        Serializable sessionId = session.getId();
        // 初始化用户的队列放到缓存里
        Deque<Serializable> onlines = (Deque<Serializable>) redisUtil.get(getRedisKickoutKey());
        if (CollectionUtils.isEmpty(onlines)) {
            onlines = new LinkedList<>();
        }
        //如果队列里没有此sessionId，且用户没有被踢出；放入队列
        if (!onlines.contains(sessionId) && session.getAttribute(KICOUTKEY) == null) {
            onlines.push(sessionId);
        }
        //如果队列里的sessionId数超出最大会话数，开始踢人
        while (onlines.size() > maxSession) {
            //first-新用户，last-旧用户
            Serializable kickoutSessionId = kickoutAfter ? onlines.removeFirst() : onlines.removeLast();
            try {
                //给下线用户session，打下线标识
                Session kickoutSession = sessionManager.getSession(new DefaultSessionKey(kickoutSessionId));
                if (kickoutSession != null) {
                    kickoutSession.setAttribute(KICOUTKEY, true);
                }
            } catch (Exception e) {
                log.error("session 异常：{}", e.getMessage());
            }
        }
        //同步redis
        redisUtil.set(getRedisKickoutKey(), onlines);
        //如果被踢出了，直接退出，重定向到踢出后的地址
        if (session.getAttribute(KICOUTKEY) != null) {
            try {
                subject.logout();
            } catch (Exception e) {
                log.error("session 下线异常：{}", e.getLocalizedMessage());
            }
            //重定向到登录页面
            //WebUtils.issueRedirect(request, response, kickoutUrl);
            //或直接给错误提示
            this.saveRequest(request);
            Result<Object> error = Result.error("当前用户已在其他地方登录！");
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            PrintWriter writer = response.getWriter();
            writer.write(JSONUtil.toJsonPrettyStr(error));
            writer.flush();
            writer.close();
            return false;
        }else {
            //刷新
            session.touch();
        }
        return true;
    }

    //是否为静态资源
    private boolean isStaticFile(String path) {
        ResourceUrlProvider resourceUrlProvider = new ResourceUrlProvider();
        String staticUri = resourceUrlProvider.getForLookupPath(path);
        return staticUri != null;
    }
}
