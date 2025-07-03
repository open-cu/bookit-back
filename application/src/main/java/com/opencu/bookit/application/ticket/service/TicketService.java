package com.opencu.bookit.application.ticket.service;

import com.opencu.bookit.application.area.service.AreaService;
import com.opencu.bookit.application.ticket.port.out.SaveTicketPort;
import com.opencu.bookit.application.user.service.UserService;
import com.opencu.bookit.domain.model.area.Area;
import com.opencu.bookit.domain.model.ticket.Ticket;
import com.opencu.bookit.domain.model.ticket.TicketId;
import com.opencu.bookit.domain.model.ticket.TicketType;
import com.opencu.bookit.domain.model.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TicketService {
    private final SaveTicketPort saveTicketPort;
    private final UserService userService;
    private final AreaService areaService;

    public TicketService(SaveTicketPort saveTicketPort, UserService userService,
                         AreaService areaService) {
        this.saveTicketPort = saveTicketPort;
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

        return saveTicketPort.save(ticket);
    }
}
