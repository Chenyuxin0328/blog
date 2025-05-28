package com.chenyuxin.rear.module.stats.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("weekly_stats")
public class WeeklyStats {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private LocalDate statWeek;

    private Integer visitorCount;

    private Integer postCount;

    private Integer docCount;

    private LocalDateTime createTime;

}
