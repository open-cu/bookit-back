package ru.tbank.bookit.book_it_backend.DTO;

import ru.tbank.bookit.book_it_backend.DTO.AIRequestComponents.MessageDTO;
import ru.tbank.bookit.book_it_backend.DTO.AIRequestComponents.CompletionOptionsDTO;

import java.util.ArrayList;
import java.util.List;

public record AIRequest(
        String modelUri,
        CompletionOptionsDTO completionOptions,
        List<MessageDTO> messages
) {}