package com.opencu.bookit.domain.model.ticket;

import com.opencu.bookit.domain.model.area.Area;
import com.opencu.bookit.domain.model.user.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    private TicketId id;
    private User user;
    private Area area;
    private TicketType type;
    private String description;
    private LocalDateTime createdAt;
}