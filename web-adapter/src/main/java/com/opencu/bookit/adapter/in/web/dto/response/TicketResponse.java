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
        @Schema(
                description = """
                Represents current priority of the ticket.
                Possible values:
                        DEFAULT - ticket is created by user and hasn't been reviewed by admin yet,
                        LOW,
                        MEDIUM,
                        HIGH,
                        CRITICAL.
                """
        )
        TicketPriority priority,
        @Schema(
                description = """
                        Represents current status of the ticket
                        Possible values:
                                OPEN - ticket is created, but not has been handled yet,
                                IN_PROGRESS - a responsible is handling the ticket,
                                ON_HOLD - ticket handling has been suspended. Providing a reason is required,
                                RESOLVED - ticket's problem is solved, but confirmation from the sender of the ticket is required,
                                CLOSED - terminal state. Problem is solved, confirmation is received,
                                REJECTED - ticket is rejected. Providing a reason is required.
                        """
        )
        TicketStatus status,
        @Schema(
                description = "Reason of holding or rejecting the ticket. In other cases is null.",
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
