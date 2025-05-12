package ru.tbank.bookit.book_it_backend.controller;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.bookit.book_it_backend.DTO.CreateTicketRequest;
import ru.tbank.bookit.book_it_backend.model.Ticket;
import ru.tbank.bookit.book_it_backend.service.TicketService;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Operation(description = "Create new ticket")
    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody CreateTicketRequest ticketDTO) {
        Ticket createdTicket = ticketService.createTicket(
                ticketDTO.userId(),
                ticketDTO.areaId(),
                ticketDTO.type(),
                ticketDTO.description());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTicket);
    }
}