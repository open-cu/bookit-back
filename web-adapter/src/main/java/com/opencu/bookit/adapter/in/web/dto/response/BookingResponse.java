package com.opencu.bookit.adapter.in.web.dto.response;

import com.opencu.bookit.domain.model.booking.BookingStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        UUID userId,
        UUID areaId,
        UUID eventId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        int quantity,
        BookingStatus status,
        LocalDateTime createdAt
) {}