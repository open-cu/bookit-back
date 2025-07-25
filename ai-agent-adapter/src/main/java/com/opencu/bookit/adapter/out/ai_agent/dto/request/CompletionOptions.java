package com.opencu.bookit.adapter.out.ai_agent.dto.request;


public record CompletionOptions(
        Boolean stream,
        double temperature,
        String maxTokens,
        ReasoningOptions reasoningOptions
) {
    public CompletionOptions {
        if (stream == null) stream = false;
        if (temperature == 0) temperature = 0.3;
        if (maxTokens == null) maxTokens = "2000";
        if (reasoningOptions == null) reasoningOptions = new ReasoningOptions("DISABLED");
    }
}
