package ru.tbank.bookit.book_it_backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.tbank.bookit.book_it_backend.model.*;
import ru.tbank.bookit.book_it_backend.repository.TicketRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final UserService userService;
    private final AreaService areaService;

    public TicketService(TicketRepository ticketRepository, UserService userService,
                         AreaService areaService) {
        this.ticketRepository = ticketRepository;
        this.userService = userService;
        this.areaService = areaService;
    }

    public Ticket createTicket(UUID userId, UUID areaId, TicketType type, String description) {
        User user = userService.findById(userId)
                               .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Area area = areaService.findById(areaId)
                               .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Area not found"));

        Ticket ticket = new Ticket();
        TicketId ticketId = new TicketId(userId, areaId);
        ticket.setId(ticketId);
        ticket.setUser(user);
        ticket.setArea(area);
        ticket.setType(type);
        ticket.setDescription(description);
        ticket.setCreatedAt(LocalDateTime.now());

        return ticketRepository.save(ticket);
    }
}
