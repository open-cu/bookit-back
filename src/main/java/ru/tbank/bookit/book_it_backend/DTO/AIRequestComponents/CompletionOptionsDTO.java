package ru.tbank.bookit.book_it_backend.DTO.AIRequestComponents;

import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.Objects;

public record CompletionOptionsDTO(
        Boolean stream,
        double temperature,
        String maxTokens,
        ReasoningOptionsDTO reasoningOptions
) {
        public CompletionOptionsDTO {
                if (stream == null) stream = false;
                if (temperature == 0) temperature = 0.3;
                if (maxTokens == null) maxTokens = "2000";
                if (reasoningOptions == null) reasoningOptions = new ReasoningOptionsDTO("DISABLED");
        }
}
