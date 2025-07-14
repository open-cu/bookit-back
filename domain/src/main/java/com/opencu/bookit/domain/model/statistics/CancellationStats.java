package com.opencu.bookit.domain.model.statistics;

public record CancellationStats(
        String areaName,
        Long totalBookings,
        Long cancelledBookings,
        Double cancellationPercentage
) {}
