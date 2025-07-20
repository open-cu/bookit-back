package com.opencu.bookit.application.port.out.ticket;

import com.opencu.bookit.domain.model.ticket.TicketModel;
import com.opencu.bookit.domain.model.ticket.TicketType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface LoadTicketPort {
    List<TicketModel> findAll();

    Optional<TicketModel> findById(UUID id);

    Page<TicketModel> findWithFilters(TicketType type, Pageable pageable);
}
