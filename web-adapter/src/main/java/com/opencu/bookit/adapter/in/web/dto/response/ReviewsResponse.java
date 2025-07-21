package com.opencu.bookit.adapter.in.web.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewsResponse(
        UUID id,
        UUID userId,
        byte rating,
        String comment,
        LocalDateTime createdAt
){}
