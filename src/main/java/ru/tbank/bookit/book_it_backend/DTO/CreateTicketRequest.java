package ru.tbank.bookit.book_it_backend.DTO;

import lombok.Data;
import ru.tbank.bookit.book_it_backend.model.TicketType;

import java.util.UUID;

public record CreateTicketRequest(
        UUID userId,
        UUID areaId,
        TicketType type,
        String description) {}
