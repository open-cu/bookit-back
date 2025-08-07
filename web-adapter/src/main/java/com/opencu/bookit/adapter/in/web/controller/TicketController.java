package com.opencu.bookit.adapter.in.web.controller;
import com.opencu.bookit.adapter.in.web.dto.request.CreateTicketRequest;
import com.opencu.bookit.adapter.in.web.dto.response.TicketResponse;
import com.opencu.bookit.adapter.in.web.mapper.TicketResponseMapper;
import com.opencu.bookit.application.service.ticket.TicketService;
import com.opencu.bookit.domain.model.ticket.TicketModel;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    TicketService ticketService;
    TicketResponseMapper ticketResponseMapper;

    public TicketController(TicketService ticketService, TicketResponseMapper ticketResponseMapper) {
        this.ticketService = ticketService;
        this.ticketResponseMapper = ticketResponseMapper;
    }

    @Operation(description = "Create new ticket")
    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@RequestBody CreateTicketRequest ticketDTO) {
        TicketModel createdTicket = ticketService.createTicket(
                ticketDTO.userId(),
                ticketDTO.areaId(),
                ticketDTO.type(),
                ticketDTO.description(),
                ticketDTO.priority(),
                ticketDTO.status()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketResponseMapper.toResponse(createdTicket));
    }
}