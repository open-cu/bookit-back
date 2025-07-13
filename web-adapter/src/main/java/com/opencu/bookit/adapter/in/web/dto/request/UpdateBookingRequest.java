package com.opencu.bookit.adapter.in.web.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateBookingRequest(
        UUID areaId,
        LocalDateTime startTime,
        LocalDateTime endTime) {}