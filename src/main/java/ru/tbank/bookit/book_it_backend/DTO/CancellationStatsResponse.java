package ru.tbank.bookit.book_it_backend.DTO;

public record CancellationStatsResponse(
        String areaName,
        long totalBookings,
        long cancelledBookings,
        double cancellationPercentage
) {}
