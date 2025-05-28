package com.chenyuxin.rear.module.file.constant;

import lombok.Getter;

@Getter
public enum MinioBucket {
    BLOG_DOC("blog-doc"),
    FRIEND_CIRCLE("friend-circle");

    private final String bucketName;

    MinioBucket(String bucketName) {
        this.bucketName = bucketName;
    }
}
