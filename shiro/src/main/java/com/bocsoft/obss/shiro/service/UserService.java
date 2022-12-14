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
        query.ne(UserBean::getStatus, UserStatusEnum.STATUS_INVALID.getCode());
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
     * ??????????????????
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean unlockAccount(String userCode, String bankNo) {
        LambdaQueryWrapper<UserBean> query = Wrappers.lambdaQuery();
        query.eq(UserBean::getUserCode, userCode);
        query.eq(UserBean::getBankNo, bankNo);
        UserBean user = userMapper.selectOne(query);
        if (user == null) {
            log.error("??????{}???????????????????????????", userCode);
            throw new UnknownAccountException();
        }
        //??????????????????
        if (UserStatusEnum.STATUS_LOCKED.getCode().equals(user.getStatus())) {
            if (this.updateStatus(userCode, bankNo, UserStatusEnum.STATUS_EFFECT)) {
                try {
                    redisUtil.del(LoginCredentialsMatcher.getLockKey(userCode, bankNo));
                } catch (Exception e) {
                    log.error("??????????????????????????????????????????????????????");
                    throw new RuntimeException();
                }
                log.info("??????{}?????????????????????", userCode);
                return true;
            }
        }
        return false;
    }

    /**
     * ????????????????????????
     *
     * @param userCode
     * @param bankNo
     * @return
     */
    public List<PwdHistoryBean> queryPwdList(String userCode, String bankNo) {
        LambdaQueryWrapper<PwdHistoryBean> query = Wrappers.lambdaQuery();
        query.eq(PwdHistoryBean::getUserCode, userCode);
        query.eq(PwdHistoryBean::getBankNo, bankNo);
        query.orderByDesc(PwdHistoryBean::getCreateDate); //??????
        return pwdHistoryMapper.selectList(query);
    }

    public boolean addPwdHistroy(PwdHistoryBean pwdHistoryBean) {
        return pwdHistoryMapper.insert(pwdHistoryBean) > 0;
    }

    public boolean delPwdHistroyById(Long id) {
        return pwdHistoryMapper.deleteById(id) > 0;
    }

    /**
     * ????????????
     * @param userBean
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean modifPassword(UserBean userBean) {
        UserBean update = UserBean.builder().passWord(userBean.getPassWord()).salt(userBean.getSalt()).build();
        LambdaQueryWrapper<UserBean> query = Wrappers.lambdaQuery();
        query.eq(UserBean::getUserCode, userBean.getUserCode());
        query.eq(UserBean::getBankNo, userBean.getBankNo());
        query.eq(UserBean::getStatus, UserStatusEnum.STATUS_EFFECT.getCode());
        log.info("????????????");
        boolean var1 = userMapper.update(update, query) > 0;
        boolean var2 = true;
        boolean var3 = true;
        if (var1){
            //???????????????
            log.info("????????????-???????????????");
            long repetition = userProperties.getRepetition();
            PwdHistoryBean pwdHistoryBean = new PwdHistoryBean();
            BeanUtil.copyProperties(userBean, pwdHistoryBean);
            var2 = pwdHistoryMapper.insert(pwdHistoryBean) > 0;
            //??????????????????
            List<PwdHistoryBean> list = queryPwdList(pwdHistoryBean.getUserCode(), pwdHistoryBean.getBankNo());
            if (!CollectionUtils.isEmpty(list) && list.size() > repetition){
                long id = list.stream().mapToLong(PwdHistoryBean::getId).min().orElse(0L);
                if (id != 0L){
                    log.info("????????????-???????????????");
                    var3 = pwdHistoryMapper.deleteById(id) > 0;
                }
            }
        }
        if (!(var1 && var2 && var3)){
            log.warn("????????????-?????????????????????");
            throw new RuntimeException();
        }
        return true;
    }

    /**
     * ??????????????????
     * @param userCode
     * @param bankNo
     * @return
     */
    public PwdStatusEnum checkLogin(String userCode, String bankNo) {
        //?????????????????????????????????
        List<PwdHistoryBean> pwdList = this.queryPwdList(userCode, bankNo);
        if (CollectionUtils.isEmpty(pwdList)) {
            log.info("??????????????????");
            return PwdStatusEnum.PWD_FIRST;
        }
        //?????????????????????????????????
        long overdueDay = userProperties.getOverdueDay();
        PwdHistoryBean pwdHistoryBean = pwdList.stream().max(Comparator.comparing(PwdHistoryBean::getId)).get();
        LocalDateTime lastTime = pwdHistoryBean.getCreateDate();
        LocalDateTime expireTime = lastTime.plusDays(overdueDay); //??????????????????
        LocalDateTime nowTime = LocalDateTime.now(); //????????????
        if (nowTime.isAfter(expireTime)) {
            log.error("??????????????????????????????????????????");
            return PwdStatusEnum.PWD_EXPIRE;
        }
        return PwdStatusEnum.PWD_EFFECT;
    }

    /**
     * ??????????????????
     * @param userCode
     * @param bankNo
     */
    public void checkModifPwd(String userCode, String bankNo, String passWord) {
        List<PwdHistoryBean> pwdList = this.queryPwdList(userCode, bankNo);
        if (CollectionUtils.isEmpty(pwdList)) {
            //?????????n??????????????????????????????
            long repetition = userProperties.getRepetition();
            Set<String> collect = pwdList.stream().limit(repetition).map(PwdHistoryBean::getPassWord).collect(Collectors.toSet());
            if (collect.contains(passWord)) {
                log.error("?????????????????????[{}]??????????????????", repetition);
                throw new IllegalArgumentException("newPassword|?????????????????????[" + repetition + "]??????????????????");
            }
        }
    }


}
