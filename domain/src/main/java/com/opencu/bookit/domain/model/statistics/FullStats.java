package com.opencu.bookit.domain.model.statistics;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record FullStats(
        List<BookingStats> stats,
        @Schema(nullable = true)
        StatsSummary summary
) {}
