package com.chenyuxin.rear.module.post.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PagePostVo {

    private Long id;

    private String text;

    private String location;

    private Integer likeCount;

    private Integer commentCount;

    private LocalDateTime createTime;

    private List<String> imageUrls;

    private List<CommentVo> commentVos;



}
