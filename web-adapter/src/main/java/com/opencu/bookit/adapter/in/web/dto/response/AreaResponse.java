package com.opencu.bookit.adapter.in.web.dto.response;

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