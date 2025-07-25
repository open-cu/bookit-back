package com.opencu.bookit.adapter.out.ai_agent.dto.response;

import java.util.List;
import java.util.Map;

public record SqlResponse(
        List<Map<String, Object>> response
) {}
