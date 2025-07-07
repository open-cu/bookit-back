package ru.tbank.bookit.book_it_backend.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookingStatsResponse(
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        String areaName,
        long totalBookings,
        @JsonProperty("percentageChange")
        Double percentageChange
) {
}

