package com.opencu.bookit.domain.model.statistics;

import java.time.LocalDate;
import java.util.List;

public record StatsSummary(
        Long totalBookings,
        String mostPopularArea,
        Long maxBookingsInDay,
        LocalDate peakDate,
        List<AreaStats> areaStats
) {}
