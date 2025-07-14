package com.opencu.bookit.adapter.in.web.dto.request;

import com.opencu.bookit.adapter.in.web.dto.request.AIRequestComponents.CompletionOptions;
import com.opencu.bookit.adapter.in.web.dto.request.AIRequestComponents.Message;

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