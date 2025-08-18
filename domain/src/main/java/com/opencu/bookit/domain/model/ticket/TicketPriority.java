package com.opencu.bookit.domain.model.ticket;

public enum TicketPriority {
    /**
     * ticket is created by user and hasn't been reviewed by admin yet
     */
    DEFAULT,
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
