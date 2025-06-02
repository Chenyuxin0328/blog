package com.chenyuxin.rear.module.user.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SelectPageUser {
    private Long id;

    private String username;

    private String password;

    private String avatar;

    private String role; // admin, user

    private LocalDateTime createTime;

//    private LocalDateTime updateTime;
}
