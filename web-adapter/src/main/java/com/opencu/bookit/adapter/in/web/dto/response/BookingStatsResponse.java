package com.opencu.bookit.adapter.in.web.dto.response;

import java.time.LocalDate;

public record BookingStatsResponse(
        LocalDate date,
        String areaName,
        long totalBookings
) {}