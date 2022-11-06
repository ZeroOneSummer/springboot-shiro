package com.bocsoft.obss.shiro.shiro;

import java.util.Random;

public class ShiroUtil {

    /**
     * 获取指定长度的盐
     */
    public static String getSalt(int lenght){
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()";
        char[] chars = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lenght; i++) {
            int index = new Random().nextInt(chars.length);
            char aChar = chars[index];
            sb.append(aChar);
        }
        return sb.toString();
    }
}
