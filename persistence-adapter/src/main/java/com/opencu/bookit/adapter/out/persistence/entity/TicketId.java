package com.opencu.bookit.adapter.out.persistence.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Embeddable
@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class TicketId implements java.io.Serializable {
    private UUID userId;
    private UUID areaId;
}
