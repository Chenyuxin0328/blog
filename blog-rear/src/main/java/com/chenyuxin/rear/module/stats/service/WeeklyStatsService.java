package com.chenyuxin.rear.module.stats.service;

import com.chenyuxin.rear.module.stats.model.vo.GetSevenWeekStatsVo;
import com.chenyuxin.rear.module.stats.model.vo.GetTotalStatisticVo;

import java.util.List;

public interface WeeklyStatsService {
    List<GetSevenWeekStatsVo> getSevenWeekStats();
    void incrementVisitorCount();
    void incrementPostCount();
    void incrementDocCount();

    /**
     * 统计总数据
     * @return GetTotalStatisticVo
     */
    GetTotalStatisticVo getTotalStatistic();

}
