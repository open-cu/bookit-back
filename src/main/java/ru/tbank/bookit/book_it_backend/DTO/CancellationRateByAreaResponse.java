package ru.tbank.bookit.book_it_backend.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

// Для процента отмен по зонам
public record CancellationRateByAreaResponse(
        String areaName,
        long totalBookings,
        long cancelledCount,
        double cancellationRate,
        @JsonProperty("percentageChange")
        Double percentageChange
) {}
