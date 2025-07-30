package com.opencu.bookit.adapter.in.web.dto.request;

import com.opencu.bookit.domain.model.ticket.TicketType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateTicketRequest(
        @NotNull
        UUID userId,

        @NotNull
        UUID areaId,

        @NotNull
        TicketType type,

        String description) {}
