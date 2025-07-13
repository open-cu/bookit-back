package com.opencu.bookit.adapter.in.web.dto.request;

import com.opencu.bookit.adapter.in.web.dto.request.AIRequestComponents.CompletionOptions;
import com.opencu.bookit.adapter.in.web.dto.request.AIRequestComponents.Message;

import java.util.List;

public record AIRequest(
        String modelUri,
        CompletionOptions completionOptions,
        List<Message> messages
) {}
