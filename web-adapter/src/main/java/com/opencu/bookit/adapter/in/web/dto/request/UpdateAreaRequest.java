package com.opencu.bookit.adapter.in.web.dto.request;

import com.opencu.bookit.domain.model.area.AreaType;

public record UpdateAreaRequest(
        String name,
        AreaType type,
        int capacity
) {}
