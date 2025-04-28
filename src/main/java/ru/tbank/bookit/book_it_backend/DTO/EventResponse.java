package ru.tbank.bookit.book_it_backend.DTO;

import ru.tbank.bookit.book_it_backend.model.ThemeTags;

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