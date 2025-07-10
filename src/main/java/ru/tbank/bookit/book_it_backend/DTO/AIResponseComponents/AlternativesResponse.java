package ru.tbank.bookit.book_it_backend.DTO.AIResponseComponents;


import ru.tbank.bookit.book_it_backend.DTO.AIRequestComponents.MessageDTO;

public record AlternativesResponse(
        MessageDTO message,
        String status
) {}
