package com.opencu.bookit.adapter.in.web.dto.request;

import com.opencu.bookit.domain.model.contentcategory.ThemeTags;

import java.time.LocalDateTime;
import java.util.List;

public record NewsUpdateRequest(
        String title,
        String description,
        List<ThemeTags> tags
) {}
