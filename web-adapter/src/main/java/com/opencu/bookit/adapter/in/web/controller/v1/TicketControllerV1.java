package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.adapter.in.web.dto.request.CreateTicketRequest;
import com.opencu.bookit.adapter.in.web.dto.request.PatchTicketRequest;
import com.opencu.bookit.adapter.in.web.dto.response.TicketResponse;
import com.opencu.bookit.adapter.in.web.mapper.TicketResponseMapper;
import com.opencu.bookit.application.service.ticket.TicketService;
import com.opencu.bookit.domain.model.ticket.TicketModel;
import com.opencu.bookit.domain.model.ticket.TicketType;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.UUID;

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
    public ResponseEntity<Page<TicketResponse>> getAllTickets(
            @RequestParam(required = false)TicketType type,
            @RequestParam(defaultValue = "${pagination.default-page}") int page,
            @RequestParam(defaultValue = "${pagination.default-size}") int size
            ) {
        Sort.Direction direction = Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "type"));
        Page<TicketResponse> tickets = ticketService.findWithFilters(type, pageable)
                .map(ticketResponseMapper::toResponse);
        
        return ResponseEntity.ok(tickets);
    }

    @Operation(summary = "Get ticket by id")
    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketResponse> getById(
            @PathVariable UUID ticketId
    ) {
        try {
            return ResponseEntity.ok(
                    ticketResponseMapper.toResponse(
                            ticketService.findById(ticketId)
                    )
            );
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(SecurityService.getAdmin())")
    @Operation(summary = "Delete ticket found by id")
    @DeleteMapping("/{ticketId}")
    public ResponseEntity<?> deleteById(
            @PathVariable UUID ticketId
    ) {
        ticketService.deleteById(ticketId);
        return ResponseEntity.ok("Ticket successfully deleted");
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(SecurityService.getAdmin())")
    @Operation(summary = "Patches ticket by id")
    @PatchMapping("/{ticketId}")
    public ResponseEntity<TicketResponse> patchById(
            @PathVariable UUID ticketId,
            @RequestBody PatchTicketRequest patchTicketRequest
    ) {
        try {
            TicketResponse ticketResponse = ticketResponseMapper.toResponse(ticketService.patchById(
                    ticketId,
                    patchTicketRequest.type(),
                    patchTicketRequest.description()
            ));
            return ResponseEntity.ok(ticketResponse);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

}