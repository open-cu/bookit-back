package com.opencu.bookit.adapter.in.web.dto.request;

import java.util.UUID;

public record CreateReviewRequest(
        UUID userId,
        int rating,
        String comment
) {
}
