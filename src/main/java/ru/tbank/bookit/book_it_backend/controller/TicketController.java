package ru.tbank.bookit.book_it_backend.controller;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.bookit.book_it_backend.DTO.TicketCreateDTO;
import ru.tbank.bookit.book_it_backend.model.Event;
import ru.tbank.bookit.book_it_backend.model.Ticket;
import ru.tbank.bookit.book_it_backend.service.TicketService;

import java.util.List;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Operation(description = "Create new ticket")
    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody TicketCreateDTO ticketDTO) {
        Ticket createdTicket = ticketService.createTicket(
                ticketDTO.getUserId(),
                ticketDTO.getAreaId(),
                ticketDTO.getType(),
                ticketDTO.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTicket);
    }
}