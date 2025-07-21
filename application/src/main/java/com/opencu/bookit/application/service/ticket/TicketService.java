package com.opencu.bookit.application.service.ticket;

import com.opencu.bookit.application.port.out.ticket.LoadTicketPort;
import com.opencu.bookit.application.service.area.AreaService;
import com.opencu.bookit.application.port.out.ticket.SaveTicketPort;
import com.opencu.bookit.application.service.user.UserService;
import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.ticket.TicketModel;
import com.opencu.bookit.domain.model.ticket.TicketId;
import com.opencu.bookit.domain.model.ticket.TicketType;
import com.opencu.bookit.domain.model.user.UserModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class TicketService {
    private final SaveTicketPort saveTicketPort;
    private final LoadTicketPort loadTicketPort;
    private final UserService userService;
    private final AreaService areaService;

    @Value("${booking.zone-id}")
    private ZoneId zoneId;

    public TicketService(SaveTicketPort saveTicketPort, LoadTicketPort loadTicketPort, UserService userService,
                         AreaService areaService) {
        this.saveTicketPort = saveTicketPort;
        this.loadTicketPort = loadTicketPort;
        this.userService = userService;
        this.areaService = areaService;
    }

    public TicketModel createTicket(UUID userId, UUID areaId, TicketType type, String description) {
        UserModel userModel = userService.findById(userId)
                                         .orElseThrow(() -> new NoSuchElementException("User not found"));

        AreaModel areaModel = areaService.findById(areaId)
                                         .orElseThrow(() -> new NoSuchElementException("Area not found"));

        TicketModel ticketModel = new TicketModel();
        TicketId ticketId = new TicketId(userId, areaId);
        ticketModel.setId(ticketId);
        ticketModel.setUserModel(userModel);
        ticketModel.setAreaModel(areaModel);
        ticketModel.setType(type);
        ticketModel.setDescription(description);
        ticketModel.setCreatedAt(LocalDateTime.now(zoneId));

        return saveTicketPort.save(ticketModel);
    }

    public List<TicketModel> getAllTickets() {
        return new ArrayList<>(loadTicketPort.findAll());
    }
}
