package com.opencu.bookit.domain.model.statistics;

import java.time.LocalDate;

public record BookingStats(
        LocalDate date,
        String areaName,
        long totalBookings
) {}