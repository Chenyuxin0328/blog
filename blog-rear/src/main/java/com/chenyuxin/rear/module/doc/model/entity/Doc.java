package com.chenyuxin.rear.module.doc.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("doc")
public class Doc {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long backgroundId;

    private String title;

    private String general;

    private Integer categoryId;

    private String url;

    @TableField(value = "`like`")
    private Integer like;

    private Integer comment;

    private LocalDate writeTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer viewScope;

    private Integer isDelete;
}
