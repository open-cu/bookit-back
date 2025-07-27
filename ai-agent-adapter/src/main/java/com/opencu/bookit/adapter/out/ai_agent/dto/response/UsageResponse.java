package com.opencu.bookit.adapter.out.ai_agent.dto.response;

public record UsageResponse(
        String inputTextTokens,
        String completionTokens,
        String totalTokens,
        CompletionTokensDetails completionTokensDetails
) {}
