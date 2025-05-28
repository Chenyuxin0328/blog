package com.chenyuxin.rear.module.doc.model.vo;


import lombok.Data;
import java.time.LocalDate;

@Data
public class PageDocVo {

    private Integer id;

    private String backgroundUrl;

    private String title;

    private String general;

    private String categoryName;

    private String url;

    private Integer like;

    private Integer comment;

    private LocalDate writeTime;

    private Integer viewScope;
}
