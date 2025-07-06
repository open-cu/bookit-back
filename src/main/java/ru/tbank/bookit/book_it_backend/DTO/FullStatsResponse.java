package ru.tbank.bookit.book_it_backend.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FullStatsResponse<T>(
        List<T> stats,
        StatsSummaryResponse summary
) {}