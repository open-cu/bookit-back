package com.opencu.bookit.adapter.in.web.dto.request;

import com.opencu.bookit.domain.model.contentcategory.ThemeTags;
import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;

public record NewsUpdateRequest(
        @NotBlank
        String title,
        @Nullable
        String shortDescription,
        @NotBlank
        String fullDescription,
        List<ThemeTags> tags
) {}
