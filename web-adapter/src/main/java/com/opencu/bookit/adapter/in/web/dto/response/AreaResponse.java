package com.opencu.bookit.adapter.in.web.dto.response;

import com.opencu.bookit.domain.model.area.AreaFeature;
import com.opencu.bookit.domain.model.area.AreaType;
import com.opencu.bookit.domain.model.image.ImageModel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record AreaResponse(
    UUID id,
    String name,
    String description,
    AreaType type,
    Set<AreaFeature> features,
    List<ImageModel> images,
    int capacity
) {}