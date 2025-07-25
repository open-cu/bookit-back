package com.opencu.bookit.adapter.out.ai_agent.dto.response;

import com.opencu.bookit.adapter.out.ai_agent.dto.request.Message;

public record AlternativesResponse(
        Message message,
        String status
) {}
