package com.opencu.bookit.adapter.out.ai_agent.dto.request;

import java.util.ArrayList;
import java.util.List;

public class AIRequestBuilder {
    public static AIRequest createAIRequest(
            String systemPrompt,
            String userPrompt,
            String modelUri
    ) {
        CompletionOptions completionOptions = new CompletionOptions(
                null,
                0,
                null,
                null
        );
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", systemPrompt));
        messages.add(new Message("user", userPrompt));
        return new AIRequest(modelUri, completionOptions, messages);
    }
}