package ru.tbank.bookit.book_it_backend.DTO;

public record AreaStats(
        String areaName,
        long totalBookings,
        double percentageOfTotal
) {}

