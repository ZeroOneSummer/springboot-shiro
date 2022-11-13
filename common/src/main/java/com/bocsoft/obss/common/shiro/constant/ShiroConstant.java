package com.bocsoft.obss.common.shiro.constant;

public class ShiroConstant {
    public static final String HASH_ALGORITHM_NAME = "MD5";     // 加密方式
    public static final int HASH_ITERATORS = 1024;              // 设置散列次数，2^length
    public static final int SALT_LENGTH = 16;                   // 随机盐的位数
}
