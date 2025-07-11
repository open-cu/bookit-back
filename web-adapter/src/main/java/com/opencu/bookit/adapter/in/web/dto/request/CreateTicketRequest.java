package com.opencu.bookit.adapter.in.web.dto.request;

import com.opencu.bookit.domain.model.ticket.TicketType;

import java.util.UUID;

public record CreateTicketRequest(
        UUID userId,
        UUID areaId,
        TicketType type,
        String description) {}
