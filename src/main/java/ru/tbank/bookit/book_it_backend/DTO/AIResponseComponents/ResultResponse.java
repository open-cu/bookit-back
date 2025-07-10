package ru.tbank.bookit.book_it_backend.DTO.AIResponseComponents;

import java.util.List;

public record ResultResponse(
        List<AlternativesResponse> alternatives,
        UsageResponse usage,
        String modelVersion
) {}
