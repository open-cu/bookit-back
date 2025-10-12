package com.opencu.bookit.adapter.in.web.dto.request;

import com.opencu.bookit.domain.model.contentcategory.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UpdateEventRequest(
        @NotBlank
        String name,
        @Nullable
        String shortDescription,
        @NotBlank
        String fullDescription,
        List<ThemeTags> tags,
        List<ContentFormat> formats,
        List<ContentTime> times,
        List<ParticipationFormat> participationFormats,
        List<TargetAudience> targetAudiences,
        @NotNull
        LocalDateTime startTime,
        @NotNull
        LocalDateTime endTime,
        @PositiveOrZero
        int available_places,
        @NotNull
        UUID areaId
) {}
