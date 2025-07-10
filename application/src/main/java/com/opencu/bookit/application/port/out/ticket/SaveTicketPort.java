package com.opencu.bookit.application.port.out.ticket;

import com.opencu.bookit.domain.model.ticket.TicketModel;

public interface SaveTicketPort {
    TicketModel save(TicketModel ticketModel);
}
