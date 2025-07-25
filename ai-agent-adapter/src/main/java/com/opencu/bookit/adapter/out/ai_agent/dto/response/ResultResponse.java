package com.opencu.bookit.adapter.out.ai_agent.dto.response;


import java.util.List;

public record ResultResponse(
        List<AlternativesResponse> alternatives,
        UsageResponse usage,
        String modelVersion
) {}