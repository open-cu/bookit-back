package ru.tbank.bookit.book_it_backend.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookingDayOfWeekStatsResponse(
        int dayOfWeek,          // 0-6 (0=воскресенье)
        String dayName,         // "Sunday", "Monday" и т.д.
        long bookingsCount,     // количество бронирований
        @JsonProperty("percentageChange")
        Double percentageChange // процент изменения
) {}