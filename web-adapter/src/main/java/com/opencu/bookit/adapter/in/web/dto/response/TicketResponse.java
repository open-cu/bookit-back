package com.opencu.bookit.adapter.in.web.dto.response;

import com.opencu.bookit.domain.model.ticket.TicketPriority;
import com.opencu.bookit.domain.model.ticket.TicketStatus;
import com.opencu.bookit.domain.model.ticket.TicketType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record TicketResponse(
        UUID id,
        UUID userId,
        UUID areaId,
        TicketType type,
        String description,
        TicketPriority priority,
        TicketStatus status,
        @Schema(
                description = "Reason of holding or rejecting the ticket. In other cases is null",
                nullable = true
        )
        String reason,

        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        @Schema(
                description = "First response time (transition to IN_PROGRESS)",
                nullable = true
        )
        LocalDateTime firstRespondedAt,
        @Schema(
                description = "Solution time (transition to RESOLVED)",
                nullable = true
        )
        LocalDateTime resolvedAt,
        @Schema(
                description = "Closing time (transition to CLOSED)",
                nullable = true
        )
        LocalDateTime closedAt
    ) {
}
