package ru.tbank.bookit.book_it_backend.DTO;

import java.util.Set;
import java.util.UUID;

public record AreaResponse(
    UUID id,
    String name,
    String description,
    String type,
    Set<String> features,
    int capacity
) {}