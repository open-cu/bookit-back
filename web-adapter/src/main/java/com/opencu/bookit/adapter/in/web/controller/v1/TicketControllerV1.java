package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.adapter.in.web.dto.request.CreateTicketRequest;
import com.opencu.bookit.adapter.in.web.dto.response.TicketResponse;
import com.opencu.bookit.adapter.in.web.mapper.TicketResponseMapper;
import com.opencu.bookit.application.service.ticket.TicketService;
import com.opencu.bookit.domain.model.ticket.TicketModel;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketControllerV1 {
    private final TicketService ticketService;
    private final TicketResponseMapper ticketResponseMapper;

    public TicketControllerV1(TicketService ticketService, TicketResponseMapper ticketResponseMapper) {
        this.ticketService = ticketService;
        this.ticketResponseMapper = ticketResponseMapper;
    }

    @Operation(summary = "Create new ticket")
    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@RequestBody CreateTicketRequest ticketDTO) {
        TicketModel createdTicket = ticketService.createTicket(
                ticketDTO.userId(),
                ticketDTO.areaId(),
                ticketDTO.type(),
                ticketDTO.description());
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketResponseMapper.toResponse(createdTicket));
    }

    @Operation(summary = "Get all tickets")
    @GetMapping
    public ResponseEntity<List<TicketResponse>> getAllTickets() {
        List<TicketResponse> tickets = ticketService.getAllTickets()
                .stream()
                .map(ticketResponseMapper::toResponse)
                .toList();
        
        return ResponseEntity.ok(tickets);
    }
}