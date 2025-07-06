package ru.tbank.bookit.book_it_backend.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookingDayOfWeekStatsResponse(
        int dayOfWeek,
        String dayName,
        long bookingsCount,
        @JsonProperty("percentageChange")
        Double percentageChange
) {}