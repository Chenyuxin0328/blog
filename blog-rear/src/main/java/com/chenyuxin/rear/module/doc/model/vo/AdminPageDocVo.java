package com.chenyuxin.rear.module.doc.model.vo;

import lombok.Data;
import java.time.LocalDate;
@Data
public class AdminPageDocVo {
    private Long id;

    private String backgroundUrl;

    private Long backgroundId;

    private String title;

    private String general;

    private Integer categoryId;

    private String categoryName;

    private String url;

    private LocalDate writeTime;

    private Integer viewScope;
}
