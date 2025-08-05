package com.opencu.bookit.application.port.out.statstics;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LoadBookingStatsPort {
    List<Object[]> findBookingStatsBetweenDates(
            LocalDateTime start,
            LocalDateTime end);

    List<Object[]> findBookingStatsBetweenDatesAndAreas(
            LocalDateTime start,
            LocalDateTime end,
            List<String> areaNames);

    List<Object[]> findBookingStatsByDayOfWeek(
            LocalDateTime start,
            LocalDateTime end);

    List<Object[]> findBookingStatsByDayOfWeekAndAreas(
            LocalDateTime start,
            LocalDateTime end,
            List<String> areaNames);

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
    List<Object[]> findEventOverlapPercentage(UUID eventId1, UUID eventId2);
    List<Object[]> findEventOverlapPercentage(UUID eventId);

    List<Object[]> findNewUsersByCreatedAtYearMonth();
}
