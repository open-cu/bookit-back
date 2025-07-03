package com.opencu.bookit.application.ticket.port.out;

import com.opencu.bookit.domain.model.ticket.Ticket;

public interface SaveTicketPort {
    Ticket save(Ticket ticket);
}
