package com.chenyuxin.rear.test;

import com.chenyuxin.rear.module.stats.service.WeeklyStatsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WeeklyStatsServiceTest {
    @Autowired
    private WeeklyStatsService weeklyStatsService;
    @Test
    void testIncrement(){
        weeklyStatsService.incrementVisitorCount();
    }
}
