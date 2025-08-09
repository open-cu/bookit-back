package com.opencu.bookit.domain.model.ticket;

import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.user.UserModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketModel {
    private UUID id;
    private UserModel userModel;
    private AreaModel areaModel;
    private TicketType type;
    private String description;
    private TicketPriority priority;
    private TicketStatus status;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime firstRespondedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;
}