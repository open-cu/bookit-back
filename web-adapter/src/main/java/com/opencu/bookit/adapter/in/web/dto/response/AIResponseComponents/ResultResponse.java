package com.opencu.bookit.adapter.in.web.dto.response.AIResponseComponents;

import java.util.List;

public record ResultResponse(
        List<AlternativesResponse> alternatives,
        UsageResponse usage,
        String modelVersion
) {}