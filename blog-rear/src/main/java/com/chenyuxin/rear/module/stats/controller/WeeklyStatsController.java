package com.chenyuxin.rear.module.stats.controller;

import com.chenyuxin.rear.common.model.Result;
import com.chenyuxin.rear.module.stats.model.vo.GetSevenWeekStatsVo;
import com.chenyuxin.rear.module.stats.model.vo.GetTotalStatisticVo;
import com.chenyuxin.rear.module.stats.service.WeeklyStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/weeklystats")
public class WeeklyStatsController {
    @Autowired
    private WeeklyStatsService weeklyStatsService;
    // 获取近七周的帖子数量
    @GetMapping("/week")
    public Result<List<GetSevenWeekStatsVo>> getSevenWeekStats(){
        List<GetSevenWeekStatsVo> getSevenWeekStatsVos = weeklyStatsService.getSevenWeekStats();
        return Result.success(getSevenWeekStatsVos);
    }
    @GetMapping("/visitcount")
    public Result<Void> visitCount(){
        weeklyStatsService.incrementVisitorCount();
        return Result.success();
    }
    @GetMapping("/total")
    public Result<GetTotalStatisticVo> getTotalStatistic(){
        GetTotalStatisticVo getTotalStatisticVo = weeklyStatsService.getTotalStatistic();
        return Result.success(getTotalStatisticVo);
    }
}
