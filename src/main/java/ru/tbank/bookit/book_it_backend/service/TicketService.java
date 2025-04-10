package ru.tbank.bookit.book_it_backend.service;

import org.springframework.stereotype.Service;
import ru.tbank.bookit.book_it_backend.model.Area;
import ru.tbank.bookit.book_it_backend.repository.TicketRepository;

import java.util.List;

@Service
public class TicketService {
    AreaService areaService;
    TicketRepository ticketRepository;

    public TicketService(AreaService areaService, TicketRepository ticketRepository) {
        this.areaService = areaService;
        this.ticketRepository = ticketRepository;
    }

    public List<String> findAllAreaNames() {
        return areaService.findAllAreaNames();
    }
}
