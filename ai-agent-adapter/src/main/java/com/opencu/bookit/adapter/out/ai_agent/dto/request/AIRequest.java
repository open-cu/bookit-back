package com.opencu.bookit.adapter.out.ai_agent.dto.request;

import java.util.List;

public record AIRequest(
        String modelUri,
        CompletionOptions completionOptions,
        List<Message> messages
) {}
