package com.opencu.bookit.adapter.in.web.dto.response.AIResponseComponents;

public record UsageResponse(
        String inputTextTokens,
        String completionTokens,
        String totalTokens,
        CompletionTokensDetails completionTokensDetails
) {}
