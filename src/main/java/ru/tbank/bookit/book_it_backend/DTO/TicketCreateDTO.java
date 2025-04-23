package ru.tbank.bookit.book_it_backend.DTO;

import lombok.Data;
import ru.tbank.bookit.book_it_backend.model.TicketType;

import java.util.UUID;

@Data
public class TicketCreateDTO {
    private UUID userId;
    private UUID areaId;
    private TicketType type;
    private String description;
}
