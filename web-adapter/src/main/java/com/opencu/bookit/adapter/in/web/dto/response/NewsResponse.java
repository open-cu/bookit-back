package com.opencu.bookit.adapter.in.web.dto.response;

import com.opencu.bookit.domain.model.contentcategory.ThemeTags;
import com.opencu.bookit.domain.model.image.ImageModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record NewsResponse(
    UUID id,
    String title,
    String description,
    Set<ThemeTags> tags,
    List<ImageModel> image,
    LocalDateTime createdAt
) {}
