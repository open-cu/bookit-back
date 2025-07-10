package com.opencu.bookit.adapter.out.persistence.adapter;

import com.opencu.bookit.adapter.out.persistence.mapper.TicketMapper;
import com.opencu.bookit.adapter.out.persistence.repository.TicketRepository;
import com.opencu.bookit.application.port.out.ticket.SaveTicketPort;
import com.opencu.bookit.domain.model.ticket.TicketModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketPersistenceAdapter implements SaveTicketPort {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    @Override
    public TicketModel save(TicketModel ticketModel) {
        return ticketMapper.toModel(ticketRepository.save(ticketMapper.toEntity(ticketModel)));
    }
}