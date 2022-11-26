package com.bocsoft.obss.shiro.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bocsoft.obss.common.enums.PwdStatusEnum;
import com.bocsoft.obss.common.enums.UserStatusEnum;
import com.bocsoft.obss.common.shiro.config.web.ShiroProperties;
import com.bocsoft.obss.common.util.RedisUtil;
import com.bocsoft.obss.shiro.entity.PwdHistoryBean;
import com.bocsoft.obss.shiro.entity.UserBean;
import com.bocsoft.obss.shiro.mapper.PwdHistoryMapper;
import com.bocsoft.obss.shiro.mapper.UserMapper;
import com.bocsoft.obss.shiro.shiro.LoginCredentialsMatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    PwdHistoryMapper pwdHistoryMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    ShiroProperties.UserProperties userProperties;

    public UserBean selectOne(String userCode, String bankNo) {
        LambdaQueryWrapper<UserBean> query = Wrappers.lambdaQuery();
        query.eq(UserBean::getUserCode, userCode);
        query.eq(UserBean::getBankNo, bankNo);
        query.eq(UserBean::getStatus, UserStatusEnum.STATUS_EFFECT.getCode());
        return userMapper.selectOne(query);
    }

    public boolean insert(UserBean userBean) {
        return userMapper.insert(userBean) > 0;
    }

    public boolean updateStatus(String userCode, String bankNo, UserStatusEnum statusEnum) {
        UserBean update = UserBean.builder()
                .userCode(userCode)
                .bankNo(bankNo)
                .status(statusEnum.getCode())
                .build();
        return userMapper.updateByMultiId(update) > 0;
    }

    /**
     * 手动解锁用户
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean unlockAccount(String userCode, String bankNo) {
        LambdaQueryWrapper<UserBean> query = Wrappers.lambdaQuery();
        query.eq(UserBean::getUserCode, userCode);
        query.eq(UserBean::getBankNo, bankNo);
        UserBean user = userMapper.selectOne(query);
        if (user == null) {
            log.error("用户{}不存在或状态异常！", userCode);
            throw new UnknownAccountException();
        }
        //解锁锁定状态
        if (UserStatusEnum.STATUS_LOCKED.getCode().equals(user.getStatus())) {
            if (this.updateStatus(userCode, bankNo, UserStatusEnum.STATUS_EFFECT)) {
                try {
                    redisUtil.del(LoginCredentialsMatcher.getLockKey(userCode, bankNo));
                } catch (Exception e) {
                    log.error("清除锁定用户缓存失败，回滚解锁状态！");
                    throw new RuntimeException();
                }
                log.info("用户{}手动解锁成功！", userCode);
                return true;
            }
        }
        return false;
    }

    /**
     * 查询密码修改历史
     *
     * @param userCode
     * @param bankNo
     * @return
     */
    public List<PwdHistoryBean> queryPwdList(String userCode, String bankNo) {
        LambdaQueryWrapper<PwdHistoryBean> query = Wrappers.lambdaQuery();
        query.eq(PwdHistoryBean::getUserCode, userCode);
        query.eq(PwdHistoryBean::getBankNo, bankNo);
        query.orderByDesc(PwdHistoryBean::getCreateDate); //倒序
        return pwdHistoryMapper.selectList(query);
    }

    public boolean addPwdHistroy(PwdHistoryBean pwdHistoryBean) {
        return pwdHistoryMapper.insert(pwdHistoryBean) > 0;
    }

    public boolean delPwdHistroyById(Long id) {
        return pwdHistoryMapper.deleteById(id) > 0;
    }

    /**
     * 密码修改
     * @param userBean
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean modifPassword(UserBean userBean) {
        UserBean update = UserBean.builder().passWord(userBean.getPassWord()).build();
        LambdaQueryWrapper<UserBean> query = Wrappers.lambdaQuery();
        query.eq(UserBean::getUserCode, userBean.getUserCode());
        query.eq(UserBean::getBankNo, userBean.getBankNo());
        query.eq(UserBean::getStatus, UserStatusEnum.STATUS_EFFECT.getCode());
        log.info("修改密码");
        boolean var1 = userMapper.update(update, query) > 0;
        boolean var2 = true;
        boolean var3 = true;
        if (var1){
            //插入历史表
            log.info("修改密码-插入历史表");
            long repetition = userProperties.getRepetition();
            PwdHistoryBean pwdHistoryBean = new PwdHistoryBean();
            BeanUtil.copyProperties(userBean, pwdHistoryBean);
            List<PwdHistoryBean> list = queryPwdList(pwdHistoryBean.getUserCode(), pwdHistoryBean.getBankNo());
            //删除多余记录
            if (!CollectionUtils.isEmpty(list) && list.size() > repetition){
                long id = list.stream().mapToLong(PwdHistoryBean::getId).min().orElse(0L);
                if (id != 0L){
                    log.info("修改密码-删除旧记录");
                    var3 = pwdHistoryMapper.deleteById(id) > 0;
                }
            }
        }
        if (!(var1 && var2 && var3)){
            log.warn("修改密码-触发事务回滚！");
            throw new RuntimeException();
        }
        return true;
    }

    /**
     * 密码状态检查
     * @param userCode
     * @param bankNo
     * @return
     */
    public PwdStatusEnum checkLogin(String userCode, String bankNo) {
        //初次登录，必须修改密码
        List<PwdHistoryBean> pwdList = this.queryPwdList(userCode, bankNo);
        if (CollectionUtils.isEmpty(pwdList)) {
            log.info("用户初次登录");
            return PwdStatusEnum.PWD_FIRST;
        }
        //查询最新密码，是否过期
        long overdueDay = userProperties.getOverdueDay();
        PwdHistoryBean pwdHistoryBean = pwdList.stream().max(Comparator.comparing(PwdHistoryBean::getId)).get();
        LocalDateTime lastTime = pwdHistoryBean.getCreateDate();
        LocalDateTime expireTime = lastTime.plusDays(overdueDay); //密码失效时间
        LocalDateTime nowTime = LocalDateTime.now(); //当前时间
        if (nowTime.isAfter(expireTime)) {
            log.error("用户密码已过期，请重置密码！");
            return PwdStatusEnum.PWD_EXPIRE;
        }
        return PwdStatusEnum.PWD_EFFECT;
    }

    /**
     * 密码修改检查
     * @param userCode
     * @param bankNo
     */
    public void checkModifPwd(String userCode, String bankNo, String passWord) {
        List<PwdHistoryBean> pwdList = this.queryPwdList(userCode, bankNo);
        if (CollectionUtils.isEmpty(pwdList)) {
            //查询前n条密码记录，是否重复
            long repetition = userProperties.getRepetition();
            Set<String> collect = pwdList.stream().limit(repetition).map(PwdHistoryBean::getPassWord).collect(Collectors.toSet());
            if (collect.contains(passWord)) {
                log.error("新密码不能和前[{}]次修改相同！", repetition);
                throw new IllegalArgumentException("newPassword|新密码不能和前[" + repetition + "]次修改相同！");
            }
        }
    }


}
