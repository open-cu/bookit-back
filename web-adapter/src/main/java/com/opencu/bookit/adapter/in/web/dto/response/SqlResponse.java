package com.opencu.bookit.adapter.in.web.dto.response;

import java.util.List;
import java.util.Map;

public record SqlResponse(
        List<Map<String, Object>> response
) {}
