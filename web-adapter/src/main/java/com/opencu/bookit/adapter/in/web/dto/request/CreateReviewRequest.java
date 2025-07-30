package com.opencu.bookit.adapter.in.web.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateReviewRequest(
        @NotNull
        UUID userId,

        @Min(1)
        @Max(5)
        int rating,

        String comment
) {
}
