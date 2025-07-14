package com.opencu.bookit.domain.model.statistics;

public record DayOfWeekStats(
        String dayOfWeek,
        Long totalBookings,
        String areaName
) {}
