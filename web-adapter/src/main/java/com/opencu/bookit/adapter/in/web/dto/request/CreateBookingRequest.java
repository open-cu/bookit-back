package com.opencu.bookit.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.data.util.Pair;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record CreateBookingRequest(
        @NotNull
        UUID userId,

        @NotNull
        UUID areaId,

        @NotNull
        @Size(min = 1)
        Set<Pair<LocalDateTime, LocalDateTime>> timePeriods,

        @Positive
        int quantity) {}
