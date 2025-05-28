package com.chenyuxin.rear.module.doc.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateDocDto {

    @NotNull(message = "背景图不能为空")
    private Long backgroundId;

    @NotEmpty(message = "标题不能为空")
    private String title;

    @NotEmpty(message = "概括不能为空")
    private String general;

    @NotNull(message = "分类ID不能为空")
    private Integer categoryId;

    @NotEmpty(message = "博客网址不能为空")
    private String url;

    private LocalDate writeTime;

    @NotNull(message = "可见范围不能为空")
    @Min(value = 1, message = "可见范围只能是 1, 2 或 3")
    @Max(value = 3, message = "可见范围只能是 1, 2 或 3")
    private Integer viewScope;
}
