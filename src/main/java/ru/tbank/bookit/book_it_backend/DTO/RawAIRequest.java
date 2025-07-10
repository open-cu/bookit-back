package ru.tbank.bookit.book_it_backend.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record RawAIRequest(
        @JsonProperty("user_id")
        UUID userId,
        String prompt
) {}
