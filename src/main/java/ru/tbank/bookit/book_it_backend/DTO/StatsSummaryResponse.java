package ru.tbank.bookit.book_it_backend.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

public record StatsSummaryResponse(
        long totalBookings,
        String mostPopularArea,
        long maxBookingsInDay,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate peakDate,
        List<AreaStats> areaStats
) {}
