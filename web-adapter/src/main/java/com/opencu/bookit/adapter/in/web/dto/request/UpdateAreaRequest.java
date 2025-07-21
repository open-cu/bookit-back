package com.opencu.bookit.adapter.in.web.dto.request;

import com.opencu.bookit.domain.model.area.AreaType;

import java.util.List;

public record UpdateAreaRequest(
        String name,
        AreaType type,
        List<String> keys,
        int capacity
) {}
