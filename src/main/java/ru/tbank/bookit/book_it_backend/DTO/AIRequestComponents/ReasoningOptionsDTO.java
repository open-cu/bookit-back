package ru.tbank.bookit.book_it_backend.DTO.AIRequestComponents;

import org.springframework.boot.context.properties.bind.DefaultValue;

public record ReasoningOptionsDTO(
        @DefaultValue("DISABLED")
        String mode
) {}
