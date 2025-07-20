package com.opencu.bookit.application.service.ticket;

import com.opencu.bookit.application.port.out.ticket.DeleteTicketPort;
import com.opencu.bookit.application.port.out.ticket.LoadTicketPort;
import com.opencu.bookit.application.service.area.AreaService;
import com.opencu.bookit.application.port.out.ticket.SaveTicketPort;
import com.opencu.bookit.application.service.user.UserService;
import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.ticket.TicketModel;
import com.opencu.bookit.domain.model.ticket.TicketType;
import com.opencu.bookit.domain.model.user.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TicketService {
    private final SaveTicketPort saveTicketPort;
    private final LoadTicketPort loadTicketPort;
    private final DeleteTicketPort deleteTicketPort;
    private final UserService userService;
    private final AreaService areaService;

    public TicketService(SaveTicketPort saveTicketPort, LoadTicketPort loadTicketPort, DeleteTicketPort deleteTicketPort, UserService userService,
                         AreaService areaService) {
        this.saveTicketPort = saveTicketPort;
        this.loadTicketPort = loadTicketPort;
        this.deleteTicketPort = deleteTicketPort;
        this.userService = userService;
        this.areaService = areaService;
    }

    @Transactional
    public TicketModel createTicket(UUID userId, UUID areaId, TicketType type, String description) {
        UserModel userModel = userService.findById(userId)
                                         .orElseThrow(() -> new NoSuchElementException("User not found"));

        AreaModel areaModel = areaService.findById(areaId)
                                         .orElseThrow(() -> new NoSuchElementException("Area not found"));

        TicketModel ticketModel = new TicketModel();
        ticketModel.setUserModel(userModel);
        ticketModel.setAreaModel(areaModel);
        ticketModel.setType(type);
        ticketModel.setDescription(description);
        ticketModel.setCreatedAt(LocalDateTime.now());

        return saveTicketPort.save(ticketModel);
    }

    public List<TicketModel> getAllTickets() {
        return new ArrayList<>(loadTicketPort.findAll());
    }

    public TicketModel findById(UUID ticketId) {
        Optional<TicketModel> ticketOpt = loadTicketPort.findById(ticketId);
        if (ticketOpt.isEmpty()) {
            throw new NoSuchElementException();
        }
        return ticketOpt.get();
    }

    @Transactional
    public void deleteById(UUID ticketId) {
        deleteTicketPort.deleteById(ticketId);
    }

    @Transactional
    public TicketModel patchById(
            UUID ticketId,
            TicketType type,
            String description
    ) {
        Optional<TicketModel> ticketOpt = loadTicketPort.findById(ticketId);
        if (ticketOpt.isEmpty()) {
            throw new NoSuchElementException();
        }
        TicketModel ticket = ticketOpt.get();
        if (type != null) ticket.setType(type);
        if (description != null) ticket.setDescription(description);
        return saveTicketPort.save(ticket);
    }

    public Page<TicketModel> findWithFilters(
            TicketType type,
            Pageable pageable
    ) {
        return loadTicketPort.findWithFilters(type, pageable);
    }
}
