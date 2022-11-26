package com.bocsoft.obss.shiro.entity;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 登录token，方便携带其他参数
 */
public class LoginTokenBean extends UsernamePasswordToken {
    private String bankNo;

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public LoginTokenBean(String userCode, String passWord, String bankNo) {
        super(userCode, passWord);
        this.bankNo = bankNo;
    }

    @Override
    public void clear() {
        super.clear();
        this.bankNo = null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (this.bankNo != null) {
            sb.append(", bankNo=").append(this.bankNo);
        }
        return sb.toString();
    }
}

