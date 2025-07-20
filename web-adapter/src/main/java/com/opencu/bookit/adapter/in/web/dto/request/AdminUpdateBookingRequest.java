package com.opencu.bookit.adapter.in.web.dto.request;

import com.opencu.bookit.domain.model.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminUpdateBookingRequest(
        UUID userId,
        UUID areaId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        BookingStatus status
) {}
