package com.opencu.bookit.domain.model.ticket;

import io.swagger.v3.oas.annotations.media.Schema;

public enum TicketPriority {
    @Schema(description = "ticket is created by user and hasn't been reviewed by admin yet")
    DEFAULT,
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
