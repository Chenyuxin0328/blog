package com.chenyuxin.rear.common.util;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class WeekUtils {
    public static LocalDate getThisMondayDate() {
        LocalDate today = LocalDate.now();
        return today.with(DayOfWeek.MONDAY);
    }
}