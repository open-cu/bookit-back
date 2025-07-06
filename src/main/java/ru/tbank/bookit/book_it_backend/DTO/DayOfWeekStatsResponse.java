package ru.tbank.bookit.book_it_backend.DTO;

public record DayOfWeekStatsResponse(
        String dayOfWeek,
        long totalBookings,
        String areaName
) {}
