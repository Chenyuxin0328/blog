package com.chenyuxin.rear.module.post.model.vo;

import lombok.Data;

@Data
public class CommentVo {
    private Long commentId;
    private Integer userId;
    private String username;
    private String avatar;
    private String content;
}
