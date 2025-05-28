package com.chenyuxin.rear.module.stats.task;

import com.chenyuxin.rear.module.stats.mapper.WeeklyStatsMapper;
import com.chenyuxin.rear.module.stats.model.entity.WeeklyStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyStatsTask {

    private final WeeklyStatsMapper weeklyStatsMapper;

    /**
     * 每周一凌晨0点执行统计任务
     */
    @Scheduled(cron = "0 0 0 ? * MON")
//    @Scheduled(cron = "0 56 17 ? * TUE")
    public void calculateWeeklyStats() {
        LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));

        WeeklyStats stats = new WeeklyStats();
        stats.setStatWeek(monday);
        stats.setPostCount(0);
        stats.setVisitorCount(0);
        stats.setDocCount(0);
        stats.setCreateTime(LocalDateTime.now());

        // 覆盖或新增该周数据
        weeklyStatsMapper.insertOrUpdate(stats);

        log.info("✅ 本周统计已更新：{}", stats);
    }
}
