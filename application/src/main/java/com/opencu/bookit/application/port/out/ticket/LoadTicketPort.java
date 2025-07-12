package com.opencu.bookit.application.port.out.ticket;

import com.opencu.bookit.domain.model.ticket.TicketModel;

import java.util.List;

public interface LoadTicketPort {
    List<TicketModel> findAll();
}
