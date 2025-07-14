package com.opencu.bookit.adapter.in.web.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record RawAIRequest(
        @JsonProperty("user_id")
        UUID userId,
        String prompt
) {}