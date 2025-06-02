package com.chenyuxin.rear.module.doc.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateDocDto {
    private Long id;

    private Long backgroundId;

    private String title;

    private String general;

    private Integer categoryId;

    private String url;

    private LocalDate writeTime;

    private Integer viewScope;
}
