package com.opencu.bookit.adapter.in.web.dto.response;

import com.opencu.bookit.domain.model.contentcategory.ContentFormat;
import com.opencu.bookit.domain.model.contentcategory.ContentTime;
import com.opencu.bookit.domain.model.contentcategory.ParticipationFormat;
import com.opencu.bookit.domain.model.contentcategory.ThemeTags;
import com.opencu.bookit.domain.model.image.ImageModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record EventResponseV1(
        UUID id,
        String name,
        String description,
        Set<ThemeTags> tags,
        Set<ContentFormat> formats,
        Set<ContentTime> times,
        Set<ParticipationFormat> participationFormats,
        List<ImageModel> images,
        LocalDateTime startTime,
        LocalDateTime endTime,
        int availablePlaces,
        UUID areaId
) {}
