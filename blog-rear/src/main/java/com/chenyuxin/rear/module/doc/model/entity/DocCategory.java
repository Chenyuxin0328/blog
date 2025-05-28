package com.chenyuxin.rear.module.doc.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("doc_category")
public class DocCategory {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String categoryName;

    private Integer count;

    private Integer isDelete;

    private LocalDateTime createTime;
}
