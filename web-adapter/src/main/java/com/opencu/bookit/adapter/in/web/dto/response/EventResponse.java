package com.opencu.bookit.adapter.in.web.dto.response;

import com.opencu.bookit.domain.model.contentcategory.*;
import com.opencu.bookit.domain.model.image.ImageModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record EventResponse(
        UUID id,
        String name,
        String description,
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