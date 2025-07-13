package com.opencu.bookit.adapter.in.web.dto.request;

import org.springframework.data.util.Pair;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record CreateBookingRequest(
        UUID userId,
        UUID areaId,
        Set<Pair<LocalDateTime, LocalDateTime>> timePeriods,
        int quantity) {}
