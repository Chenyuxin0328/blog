package com.chenyuxin.rear.module.doc.model.vo;

import lombok.Data;

import java.time.LocalDate;
@Data
public class GetRecentDocVo {
    private Long id;
    private LocalDate writeTime;
    private String title;
    private String url;
}
