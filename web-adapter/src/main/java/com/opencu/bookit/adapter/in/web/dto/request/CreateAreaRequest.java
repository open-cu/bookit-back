package com.opencu.bookit.adapter.in.web.dto.request;

import com.opencu.bookit.domain.model.area.AreaFeature;
import com.opencu.bookit.domain.model.area.AreaStatus;
import com.opencu.bookit.domain.model.area.AreaType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CreateAreaRequest(
        @Schema(
                requiredMode = Schema.RequiredMode.REQUIRED,
                type = "String"
        )
        String name,

        @Schema(
                requiredMode = Schema.RequiredMode.REQUIRED,
                type = "String"
        )
        String description,

        @Schema(
                requiredMode = Schema.RequiredMode.REQUIRED,
                type = "String"
        )
        AreaType type,

        @Schema(
                requiredMode = Schema.RequiredMode.REQUIRED,
                type = "array"
        )
        List<AreaFeature> features,

        @Schema(
                requiredMode = Schema.RequiredMode.REQUIRED,
                type = "Integer"
        )
        int capacity,

        @Schema(
                requiredMode = Schema.RequiredMode.REQUIRED,
                type = "String"
        )
        AreaStatus status
) {}
