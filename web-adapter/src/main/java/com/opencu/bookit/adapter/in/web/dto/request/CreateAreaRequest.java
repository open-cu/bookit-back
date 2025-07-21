package com.opencu.bookit.adapter.in.web.dto.request;

import com.opencu.bookit.domain.model.area.AreaFeature;
import com.opencu.bookit.domain.model.area.AreaStatus;
import com.opencu.bookit.domain.model.area.AreaType;

import java.util.List;

public record CreateAreaRequest(
        String name,
        String description,
        AreaType type,
        List<AreaFeature> features,
        List<String> keys,
        int capacity,
        AreaStatus status
) {}
