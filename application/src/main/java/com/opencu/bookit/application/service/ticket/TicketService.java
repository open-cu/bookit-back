package com.opencu.bookit.application.service.ticket;

import com.opencu.bookit.application.service.area.AreaService;
import com.opencu.bookit.application.port.out.ticket.SaveTicketPort;
import com.opencu.bookit.application.service.user.UserService;
import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.ticket.TicketModel;
import com.opencu.bookit.domain.model.ticket.TicketId;
import com.opencu.bookit.domain.model.ticket.TicketType;
import com.opencu.bookit.domain.model.user.UserModel;
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

    public TicketModel createTicket(UUID userId, UUID areaId, TicketType type, String description) {
        UserModel userModel = userService.findById(userId)
                                         .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        AreaModel areaModel = areaService.findById(areaId)
                                         .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Area not found"));

        TicketModel ticketModel = new TicketModel();
        TicketId ticketId = new TicketId(userId, areaId);
        ticketModel.setId(ticketId);
        ticketModel.setUserModel(userModel);
        ticketModel.setAreaModel(areaModel);
        ticketModel.setType(type);
        ticketModel.setDescription(description);
        ticketModel.setCreatedAt(LocalDateTime.now());

        return saveTicketPort.save(ticketModel);
    }
}
