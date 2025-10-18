package com.opencu.bookit.adapter.in.web.dto.response;

import com.opencu.bookit.domain.model.contentcategory.*;
import com.opencu.bookit.domain.model.image.ImageModel;
import com.opencu.bookit.domain.model.user.UserStatus;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public record EventResponseV1(
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
        LocalDateTime startTime,
        LocalDateTime endTime,
        int availablePlaces,
        UUID areaId,
        boolean requiresApplication
) {}
