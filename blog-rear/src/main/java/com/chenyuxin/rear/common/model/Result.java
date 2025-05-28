package com.chenyuxin.rear.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /**
     * 状态码，约定：200 成功，非200失败
     */
    private int code;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 泛型数据，返回给前端的数据对象
     */
    private T data;

    // 成功且带数据的返回
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "成功", data);
    }

    // 成功但不带数据
    public static <T> Result<T> success() {
        return new Result<>(200, "成功", null);
    }

    // 失败返回，带自定义状态码和消息
    public static <T> Result<T> failure(int code, String message) {
        return new Result<>(code, message, null);
    }

    // 失败返回，带消息，默认状态码 500
    public static <T> Result<T> failure(String message) {
        return new Result<>(500, message, null);
    }
}
