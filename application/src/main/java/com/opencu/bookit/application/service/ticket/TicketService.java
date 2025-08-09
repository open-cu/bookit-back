package com.opencu.bookit.application.service.ticket;

import com.opencu.bookit.application.port.out.ticket.DeleteTicketPort;
import com.opencu.bookit.application.port.out.ticket.LoadTicketPort;
import com.opencu.bookit.application.service.area.AreaService;
import com.opencu.bookit.application.port.out.ticket.SaveTicketPort;
import com.opencu.bookit.application.service.user.UserService;
import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.ticket.TicketModel;
import com.opencu.bookit.domain.model.ticket.TicketPriority;
import com.opencu.bookit.domain.model.ticket.TicketStatus;
import com.opencu.bookit.domain.model.ticket.TicketType;
import com.opencu.bookit.domain.model.user.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class TicketService {
    private final SaveTicketPort saveTicketPort;
    private final LoadTicketPort loadTicketPort;
    private final DeleteTicketPort deleteTicketPort;
    private final UserService userService;
    private final AreaService areaService;

    @Value("${booking.zone-id}")
    private ZoneId zoneId;

    public TicketService(
            SaveTicketPort saveTicketPort, LoadTicketPort loadTicketPort,
            DeleteTicketPort deleteTicketPort,
            UserService userService,
            AreaService areaService
    ) {
        this.saveTicketPort = saveTicketPort;
        this.loadTicketPort = loadTicketPort;
        this.deleteTicketPort = deleteTicketPort;
        this.userService = userService;
        this.areaService = areaService;
    }

    @Transactional
    public TicketModel createTicket(
            UUID userId,
            UUID areaId,
            TicketType type,
            String description,
            TicketPriority priority,
            TicketStatus status
            ) {
        UserModel userModel = userService.findById(userId)
                                         .orElseThrow(() -> new NoSuchElementException("User not found"));

        AreaModel areaModel = areaService.findById(areaId)
                                         .orElseThrow(() -> new NoSuchElementException("Area not found"));

        TicketModel ticketModel = new TicketModel();
        ticketModel.setUserModel(userModel);
        ticketModel.setAreaModel(areaModel);
        ticketModel.setType(type);
        ticketModel.setDescription(description);
        ticketModel.setPriority(priority);
        ticketModel.setStatus(status);
        ticketModel.setCreatedAt(LocalDateTime.now(zoneId));

        return saveTicketPort.save(ticketModel);
    }

    public List<TicketModel> getAllTickets() {
        return new ArrayList<>(loadTicketPort.findAll());
    }

    public TicketModel findById(UUID ticketId) {
        Optional<TicketModel> ticketOpt = loadTicketPort.findById(ticketId);
        if (ticketOpt.isEmpty()) {
            throw new NoSuchElementException("No such ticket " + ticketId + " found");
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
            String description,
            TicketPriority priority,
            TicketStatus status,
            String reason
    ) {
        Optional<TicketModel> ticketOpt = loadTicketPort.findById(ticketId);
        if (ticketOpt.isEmpty()) {
            throw new NoSuchElementException();
        }
        TicketModel ticket = ticketOpt.get();
        if (type != null) ticket.setType(type);
        if (description != null) ticket.setDescription(description);
        if (priority != null) ticket.setPriority(priority);
        if (status != null) setStatusWithAdditionalReason(status, reason, ticket);
        ticket.setUpdatedAt(LocalDateTime.now(zoneId));
        return saveTicketPort.save(ticket);
    }

    public Page<TicketModel> findWithFilters(
            LocalDate startDate,
            LocalDate endDate,
            String search,
            TicketType type,
            Pageable pageable
    ) {
        return loadTicketPort.findWithFilters(startDate, endDate, search, type, pageable);
    }

    private void setStatusWithAdditionalReason(TicketStatus newStatus, String reason, TicketModel ticketModel) {
        validateStatusChange(ticketModel, newStatus);
        handleReason(newStatus, reason, ticketModel);
        updateTimestamps(newStatus, ticketModel);
        ticketModel.setStatus(newStatus);
        setFirstRespondedIfNeeded(ticketModel);
    }

    private void validateStatusChange(TicketModel ticketModel, TicketStatus newStatus) {
        if (ticketModel.getStatus().isTerminal()) {
            throw new IllegalArgumentException(
                    "Terminal status " + ticketModel.getStatus() + " is already set and cannot be changed"
            );
        }
    }

    private void handleReason(TicketStatus status, String reason, TicketModel ticketModel) {
        if (status.needsReason()) {
            if (reason == null || reason.isBlank()) {
                throw new IllegalArgumentException(
                        "Reason cannot be null or blank if status " + status.name() + " requires reason"
                );
            }
            ticketModel.setReason(reason);
        } else {
            ticketModel.setReason(null);
        }
    }

    private void updateTimestamps(TicketStatus status, TicketModel ticketModel) {
        if (status.isTerminal()) {
            ticketModel.setClosedAt(LocalDateTime.now(zoneId));
        }
        if (status.isResolved()) {
            ticketModel.setResolvedAt(LocalDateTime.now(zoneId));
        }
        ticketModel.setUpdatedAt(LocalDateTime.now(zoneId));
    }

    private void setFirstRespondedIfNeeded(TicketModel ticketModel) {
        if (ticketModel.getFirstRespondedAt() == null) {
            ticketModel.setFirstRespondedAt(LocalDateTime.now(zoneId));
        }
    }
}
