package com.opencu.bookit.domain.model.statistics;

public record AreaStats(
        String areaName,
        Long totalBookings,
        Double percentageOfTotal
) {}
