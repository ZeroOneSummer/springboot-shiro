package com.bocsoft.obss.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚举
 */
@Getter
@AllArgsConstructor
public enum  UserStatusEnum {

    STATUS_INVALID(-1, "失效"),
    STATUS_EFFECT(0, "正常"),
    STATUS_LOCKED(1, "锁定")
    ;

    private final Integer code;
    private final String desc;
}
