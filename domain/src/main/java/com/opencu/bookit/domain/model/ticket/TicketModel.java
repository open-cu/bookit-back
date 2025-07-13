package com.opencu.bookit.domain.model.ticket;

import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.user.UserModel;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketModel {
    private TicketId id;
    private UserModel userModel;
    private AreaModel areaModel;
    private TicketType type;
    private String description;
    private LocalDateTime createdAt;
}