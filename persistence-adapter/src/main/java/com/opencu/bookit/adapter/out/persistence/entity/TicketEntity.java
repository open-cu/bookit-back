package com.opencu.bookit.adapter.out.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencu.bookit.domain.model.ticket.TicketType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TICKETS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketEntity {

    @EmbeddedId
    private TicketId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("areaId")
    @JoinColumn(name = "area_id", nullable = false)
    @JsonIgnore
    private AreaEntity areaEntity;

    @Column(nullable = false)
    private TicketType type;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}