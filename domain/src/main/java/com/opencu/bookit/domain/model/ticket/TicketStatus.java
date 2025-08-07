package com.opencu.bookit.domain.model.ticket;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "represents current status of the ticket")
public enum TicketStatus {
    @Schema(description = "ticket is created, but not has been handled yet")
    OPEN,
    @Schema(description = "A responsible is handling the ticket")
    IN_PROGRESS,
    @Schema(description = "Ticket handling has been suspended. Providing a reason is required")
    ON_HOLD,
    @Schema(description = "ticket's problem is solved, but confirmation from the sender of the ticket is required")
    RESOLVED,
    @Schema(description = "Terminal state. Problem is solved, confirmation is received")
    CLOSED,
    @Schema(description = "Ticket is rejected. Providing a reason is required")
    REJECTED
}
