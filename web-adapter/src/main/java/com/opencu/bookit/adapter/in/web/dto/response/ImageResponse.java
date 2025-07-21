package com.opencu.bookit.adapter.in.web.dto.response;

public record ImageResponse(
        String key,             // имя/ключ файла в бакете
        String contentType,
        long contentLength,
        String eTag,
        String lastModified,
        String base64Data
) {
}
