package ru.tbank.bookit.book_it_backend.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.bookit.book_it_backend.DTO.CreateTicketRequest;
import ru.tbank.bookit.book_it_backend.model.Ticket;
import ru.tbank.bookit.book_it_backend.service.TicketService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketControllerV1 {
    private final TicketService ticketService;

    public TicketControllerV1(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Operation(summary = "Create new ticket")
    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody CreateTicketRequest ticketDTO) {
        Ticket createdTicket = ticketService.createTicket(
                ticketDTO.userId(),
                ticketDTO.areaId(),
                ticketDTO.type(),
                ticketDTO.description());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTicket);
    }

    @Operation(summary = "Get all tickets")
    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        // TODO: Реализовать получение тикетов
        return ResponseEntity.ok(List.of());
    }
}