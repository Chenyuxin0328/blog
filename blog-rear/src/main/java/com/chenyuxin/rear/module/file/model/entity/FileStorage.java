package com.chenyuxin.rear.module.file.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@TableName("file_storage")
public class FileStorage {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String url;

    private Integer status;

    private LocalDateTime createTime;
}
