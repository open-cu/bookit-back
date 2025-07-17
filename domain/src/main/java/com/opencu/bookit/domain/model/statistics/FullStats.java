package com.opencu.bookit.domain.model.statistics;


import java.util.List;

public record FullStats(
        List<BookingStats> stats,
        StatsSummary summary
) {}
