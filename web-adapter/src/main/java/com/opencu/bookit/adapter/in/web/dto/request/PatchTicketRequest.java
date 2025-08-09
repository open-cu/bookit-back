package com.opencu.bookit.adapter.in.web.dto.request;

import com.opencu.bookit.domain.model.ticket.TicketPriority;
import com.opencu.bookit.domain.model.ticket.TicketStatus;
import com.opencu.bookit.domain.model.ticket.TicketType;
import io.swagger.v3.oas.annotations.media.Schema;

public record PatchTicketRequest(
        @Schema(nullable = true)
        TicketType type,
        @Schema(nullable = true)
        String description,
        @Schema(nullable = true)
        TicketPriority priority,
        @Schema(nullable = true)
        TicketStatus status,
        @Schema(nullable = true)
        String reason
){}
