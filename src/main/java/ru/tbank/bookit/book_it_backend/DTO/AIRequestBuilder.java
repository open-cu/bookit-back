package ru.tbank.bookit.book_it_backend.DTO;

import ru.tbank.bookit.book_it_backend.DTO.AIRequestComponents.CompletionOptionsDTO;
import ru.tbank.bookit.book_it_backend.DTO.AIRequestComponents.MessageDTO;

import java.util.ArrayList;
import java.util.List;

public class AIRequestBuilder {
    public static AIRequest createAIRequest(
            String systemPrompt,
            String userPrompt,
            String modelUri
    ) {
        CompletionOptionsDTO completionOptions = new CompletionOptionsDTO(
                null,
                0,
                null,
                null
        );
        List<MessageDTO> messages = new ArrayList<>();
        messages.add(new MessageDTO("system", systemPrompt));
        messages.add(new MessageDTO("user", userPrompt));
        return new AIRequest(modelUri, completionOptions, messages);
    }
}
