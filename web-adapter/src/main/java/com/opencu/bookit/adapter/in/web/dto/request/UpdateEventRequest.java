package com.opencu.bookit.adapter.in.web.dto.request;

import com.opencu.bookit.domain.model.contentcategory.ContentFormat;
import com.opencu.bookit.domain.model.contentcategory.ContentTime;
import com.opencu.bookit.domain.model.contentcategory.ParticipationFormat;
import com.opencu.bookit.domain.model.contentcategory.ThemeTags;

import java.time.LocalDateTime;
import java.util.List;

public record UpdateEventRequest(
        String name,
        String description,
        List<ThemeTags> tags,
        List<ContentFormat> formats,
        List<ContentTime> times,
        List<ParticipationFormat> participationFormats,
        LocalDateTime date,
        LocalDateTime endTime,
        int available_places
) {}
