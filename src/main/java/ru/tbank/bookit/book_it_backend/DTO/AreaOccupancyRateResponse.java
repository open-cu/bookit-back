package ru.tbank.bookit.book_it_backend.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

// Для загруженности зон
public record AreaOccupancyRateResponse(
        String areaName,
        long bookingsCount,
        double occupancyRate,
        @JsonProperty("percentageChange")
        Double percentageChange
) {}
