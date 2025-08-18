package com.opencu.bookit.domain.model.ticket;

/**
 * Represents current status of the ticket
 */
public enum TicketStatus {
    /**
     * ticket is created, but not has been handled yet
     */
    OPEN,

    /**
     * a responsible is handling the ticket
     */
    IN_PROGRESS,

    /**
     * ticket handling has been suspended. Providing a reason is required
     */
    ON_HOLD,

    /**
     * ticket's problem is solved, but confirmation from the sender of the ticket is required
     */
    RESOLVED,

    /**
     * terminal state. Problem is solved, confirmation is received
     */
    CLOSED,

    /**
     * ticket is rejected. Providing a reason is required
     */
    REJECTED;

    public boolean isTerminal() {
        return this.equals(CLOSED);
    }

    public boolean needsReason() {
        return this.equals(REJECTED) ||  this.equals(ON_HOLD);
    }

    public boolean isResolved() {
        return this.equals(RESOLVED);
    }
}
