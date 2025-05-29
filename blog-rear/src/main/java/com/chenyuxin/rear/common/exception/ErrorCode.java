package com.chenyuxin.rear.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SYSTEM_ERROR(500, "系统异常"),
    PARAMETER_ERROR(400, "参数错误"),
    AUTH_FAIL(401, "认证失败"),
    UPLOAD_IMAGE_FAIL(501,"上传文件失败"),
    DELETE_CATEGORY_FAIL(502,"无法删除分类，请先删除所属分类的帖子"),
    LOGIN_FAIL(503,"用户名或密码错误"),
    USERNAME_SAME(504,"用户名重复");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
