package com.chenyuxin.rear.common.enums;

import lombok.Getter;

@Getter
public enum AccessPermission {
    ADMIN(3),
    USER(2),
    GUEST(1);
    private final Integer permCode;

    AccessPermission(Integer permCode){
        this.permCode = permCode;
    }

}
