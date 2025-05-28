package com.chenyuxin.rear.module.stats.model.vo;

import lombok.Data;

@Data
public class GetTotalStatisticVo {
    private Integer total_view;
    private Integer total_doc;
    private Integer total_post;
    private Integer total_like;
    private Integer total_comment;
}
