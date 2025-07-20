package com.opencu.bookit.application.port.out.ticket;

import java.util.UUID;

public interface DeleteTicketPort {
    void deleteById(UUID ticketId);
}
