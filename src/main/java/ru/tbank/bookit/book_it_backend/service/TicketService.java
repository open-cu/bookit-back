package ru.tbank.bookit.book_it_backend.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    public List<Ticket> findAll() {
        return (List<Ticket>) ticketRepository.findAll();
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

    @Transactional
    public Ticket updateTicket(UUID ticketId, TicketType type, String description) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        if (type != null && !type.equals(ticket.getType())) {
            ticket.setType(type);
        }
        if (description != null && !description.equals(ticket.getDescription())) {
            ticket.setDescription(description);
        }

        return ticketRepository.save(ticket);
    }

    @Transactional
    public void deleteTicket(UUID ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new EntityNotFoundException("Ticket not found");
        }
        ticketRepository.deleteById(ticketId);
    }

}
