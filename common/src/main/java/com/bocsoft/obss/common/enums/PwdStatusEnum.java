package com.bocsoft.obss.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 密码状态枚举
 */
@Getter
@AllArgsConstructor
public enum PwdStatusEnum {

    PWD_EXPIRE(-1, "过期"),
    PWD_FIRST(0, "首次登录"),
    PWD_EFFECT(1, "正常")
    ;

    private final Integer code;
    private final String desc;
}
