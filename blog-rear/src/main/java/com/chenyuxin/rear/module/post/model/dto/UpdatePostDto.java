package com.chenyuxin.rear.module.post.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UpdatePostDto {
    private Long id;

    private String text;

    private String location;

    private Integer viewScope;

    private LocalDateTime createTime;

    private List<Long> imageIds;
}
