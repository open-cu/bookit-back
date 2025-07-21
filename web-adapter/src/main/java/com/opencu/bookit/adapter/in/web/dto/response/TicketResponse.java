package com.opencu.bookit.adapter.in.web.dto.response;

import com.opencu.bookit.domain.model.ticket.TicketType;

import java.util.UUID;

public record TicketResponse(
        UUID id,
        UUID userId,
        UUID areaId,
        TicketType type,
        String description) {
}
