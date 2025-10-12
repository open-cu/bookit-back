package com.opencu.bookit.adapter.in.web.dto.response;

import com.opencu.bookit.domain.model.contentcategory.ThemeTags;
import com.opencu.bookit.domain.model.image.ImageModel;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public record NewsResponse(
    UUID id,
    String title,
    @Nullable
    String shortDescription,
    String fullDescription,
    Set<ThemeTags> tags,
    List<ImageModel> images,
    LocalDateTime createdAt
) {}
