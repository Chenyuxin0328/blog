package com.chenyuxin.rear.module.post.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("post")
public class Post {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String text;

    private String location;

    private Integer viewScope;

    private Integer likeCount;

    private Integer commentCount;

    private LocalDateTime createTime;

    private Integer isDelete; // 0=未删除, 1=已删除
}
