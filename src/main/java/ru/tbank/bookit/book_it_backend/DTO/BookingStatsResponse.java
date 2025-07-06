package ru.tbank.bookit.book_it_backend.DTO;

import java.time.LocalDate;

public record BookingStatsResponse(
        LocalDate date,
        String areaName,
        long totalBookings
) {}