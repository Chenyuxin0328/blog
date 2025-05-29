package com.chenyuxin.rear.module.user.model.dto;

import lombok.Data;

@Data
public class GetUserInfoDto {
    private Long id;
    private String username;
    private String avatar;
    private String role;
}
