package ru.tbank.bookit.book_it_backend.DTO;

import java.util.List;
import java.util.Map;

public record SqlResponseDTO(
        List<Map<String, Object>> response
) {}
