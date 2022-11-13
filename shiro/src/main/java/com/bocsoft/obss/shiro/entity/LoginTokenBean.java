package com.bocsoft.obss.shiro.entity;

import org.apache.shiro.authc.UsernamePasswordToken;

public class LoginTokenBean extends UsernamePasswordToken {
    private String bankNo;

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public LoginTokenBean(String username, String password, String bankNo) {
        super(username, password);
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

