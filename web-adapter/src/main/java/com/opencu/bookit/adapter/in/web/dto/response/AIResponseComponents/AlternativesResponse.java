package com.opencu.bookit.adapter.in.web.dto.response.AIResponseComponents;

import com.opencu.bookit.adapter.in.web.dto.request.AIRequestComponents.Message;

public record AlternativesResponse(
        Message message,
        String status
) {}
