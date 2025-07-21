package com.opencu.bookit.domain.model.image;

public record ImageModel(
        String key,
        String contentType,
        long contentLength,
        String eTag,
        String lastModified,
        String base64Data
) {}