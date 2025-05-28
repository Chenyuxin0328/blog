package com.chenyuxin.rear.module.stats.service.Impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chenyuxin.rear.common.util.WeekUtils;
import com.chenyuxin.rear.module.post.mapper.PostMapper;
import com.chenyuxin.rear.module.stats.mapper.WeeklyStatsMapper;
import com.chenyuxin.rear.module.stats.model.entity.WeeklyStats;
import com.chenyuxin.rear.module.stats.model.vo.GetSevenWeekStatsVo;
import com.chenyuxin.rear.module.stats.model.vo.GetTotalStatisticVo;
import com.chenyuxin.rear.module.stats.service.WeeklyStatsService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class WeeklyStatsServiceImpl implements WeeklyStatsService {
    @Autowired
    private WeeklyStatsMapper weeklyStatsMapper;
    @Autowired
    private PostMapper postMapper;
    @Override
    public List<GetSevenWeekStatsVo> getSevenWeekStats() {
        return weeklyStatsMapper.selectRecentSevenWeeks();
    }
    @Override
    public void incrementVisitorCount(){
        LambdaUpdateWrapper<WeeklyStats> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(WeeklyStats::getStatWeek, WeekUtils.getThisMondayDate())
                .setSql("visitor_count =  visitor_count + 1");
        weeklyStatsMapper.update(wrapper);
    }
    @Override
    public void incrementPostCount(){
        LambdaUpdateWrapper<WeeklyStats> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(WeeklyStats::getStatWeek, WeekUtils.getThisMondayDate())
                .setSql("post_count =  post_count + 1");
        weeklyStatsMapper.update(wrapper);
    }
    @Override
    public void incrementDocCount(){
        LambdaUpdateWrapper<WeeklyStats> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(WeeklyStats::getStatWeek, WeekUtils.getThisMondayDate())
                .setSql("doc_count =  doc_count + 1");
        weeklyStatsMapper.update(wrapper);
    }
    // 统计总数据
    @Override
    public GetTotalStatisticVo getTotalStatistic() {
        GetTotalStatisticVo getTotalStatisticVo = weeklyStatsMapper.getTotalStatistic();
        Map<String, Object> stats = postMapper.getLikeAndCommentStats();
        Integer likeCount = stats.get("SUM(like_count)") != null ? ((Number) stats.get("SUM(like_count)")).intValue() : 0;
        Integer commentCount = stats.get("SUM(comment_count)") != null ? ((Number) stats.get("SUM(comment_count)")).intValue() : 0;
        getTotalStatisticVo.setTotal_like(likeCount);
        getTotalStatisticVo.setTotal_comment(commentCount);
        return getTotalStatisticVo;
    }
}
