package com.opencu.bookit.adapter.in.web.dto.request;

import com.opencu.bookit.domain.model.booking.BookingStatus;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateBookingRequest(
        @Nullable
        UUID userId,
        UUID areaId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        @Nullable
        BookingStatus status) {}