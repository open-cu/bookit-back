package com.opencu.bookit.domain.model.ticket;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class TicketId {
    private UUID userId;
    private UUID areaId;
}
