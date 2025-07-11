package com.opencu.bookit.adapter.in.web.dto.response;

import com.opencu.bookit.domain.model.event.ThemeTags;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record EventResponse(
        UUID id,
        String name,
        String description,
        Set<ThemeTags> tags,
        LocalDateTime date,
        int availablePlaces,
        Set<UUID> registeredUsers
) {}