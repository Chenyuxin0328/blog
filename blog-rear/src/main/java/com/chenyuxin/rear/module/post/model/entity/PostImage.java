package com.chenyuxin.rear.module.post.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("post_image")
public class PostImage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long postId;

    private Long fileId;

    private Integer sortOrder;
}
