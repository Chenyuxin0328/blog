package com.chenyuxin.rear.module.stats.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenyuxin.rear.module.stats.model.entity.WeeklyStats;
import com.chenyuxin.rear.module.stats.model.vo.GetSevenWeekStatsVo;
import com.chenyuxin.rear.module.stats.model.vo.GetTotalStatisticVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WeeklyStatsMapper extends BaseMapper<WeeklyStats> {
    List<GetSevenWeekStatsVo> selectRecentSevenWeeks();

    GetTotalStatisticVo getTotalStatistic();

}
