package com.opencu.bookit.domain.model.statistics;

public record BusiestHours(
        Integer hour,
        Long bookingsCount
) {}
