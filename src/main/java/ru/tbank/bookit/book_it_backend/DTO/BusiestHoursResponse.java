package ru.tbank.bookit.book_it_backend.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

public record BusiestHoursResponse(
        @Schema(description = "Hour of day (0-23)", example = "14")
        int hour,

        @Schema(description = "Number of bookings in this hour", example = "42")
        long bookingsCount
) {}
