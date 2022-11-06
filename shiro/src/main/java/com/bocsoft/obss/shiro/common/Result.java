package com.bocsoft.obss.shiro.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 公共响应类
 */
@ApiModel("响应实体")
@Setter
@Getter
public class Result<T> {

    private static final int CODE_SUCCESS = 200;
    private static final int CODE_ERROR = 500;
    private static final int CODE_NO_LOGIN = 300;

    @ApiModelProperty(value="响应码")
    private int code;
    @ApiModelProperty(value="响应信息")
    private String msg;
    @ApiModelProperty(value="响应内容")
    private T data;

    private Result(int code, String msg, T data) {
        this.setCode(code);
        this.setMsg(msg);
        this.setData(data);
    }

    public static <T> Result<T> success() {
        return new Result<T>(CODE_SUCCESS, "success", null);
    }

    public static <T> Result<T> success(String message) {
        return new Result<T>(CODE_SUCCESS, message, null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<T>(CODE_SUCCESS, "success", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<T>(CODE_SUCCESS, message, data);
    }

    public static <T> Result<T> error() {
        return new Result<T>(CODE_ERROR, "fail", null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<T>(CODE_ERROR, message, null);
    }

    public static <T> Result<T> error(T data) {
        return new Result<T>(CODE_ERROR, "fail", data);
    }

    public static <T> Result<T> error(String message, T data) {
        return new Result<T>(CODE_ERROR, message, data);
    }

    public static <T> Result<T> noLogin(String message) {
        return new Result<T>(CODE_NO_LOGIN, message, null);
    }
}