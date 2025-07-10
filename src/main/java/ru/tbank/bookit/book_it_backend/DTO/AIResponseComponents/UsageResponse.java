package ru.tbank.bookit.book_it_backend.DTO.AIResponseComponents;

public record UsageResponse(
        String inputTextTokens,
        String completionTokens,
        String totalTokens,
        CompletionTokensDetails completionTokensDetails
) {}
