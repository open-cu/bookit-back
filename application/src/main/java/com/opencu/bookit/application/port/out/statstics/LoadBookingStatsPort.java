package com.opencu.bookit.application.port.out.statstics;

import java.time.LocalDateTime;
import java.util.List;

public interface LoadBookingStatsPort {
    List<Object[]> findBookingStatsBetweenDates(
            LocalDateTime start,
            LocalDateTime end
    );

    List<Object[]> findBookingStatsByDayOfWeek(
            LocalDateTime start,
            LocalDateTime end
    );

    List<Object[]> findCancellationStatsByArea(
            LocalDateTime start,
            LocalDateTime end
    );

    List<Object[]> findBusiestHours(
            LocalDateTime start,
            LocalDateTime end,
            String areaName
    );

    List<Object[]> findEventOverlapPercentage();
    List<Object[]> findNewUsersByCreatedAtYearMonth();
}
