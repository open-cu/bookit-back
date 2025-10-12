package com.opencu.bookit.adapter.in.web.dto.response;

import com.opencu.bookit.domain.model.contentcategory.*;
import com.opencu.bookit.domain.model.image.ImageModel;
import jakarta.validation.constraints.Null;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public record EventResponse(
        UUID id,
        String name,
        @Nullable
        String shortDescription,
        String fullDescription,
        Set<ThemeTags> tags,
        Set<ContentFormat> formats,
        Set<ContentTime> times,
        Set<ParticipationFormat> participationFormats,
        Set<TargetAudience> targetAudiences,
        List<ImageModel> images,
        LocalDateTime date,
        LocalDateTime endTime,
        //для сохранения старой версии API
        int availablePlaces
) {}