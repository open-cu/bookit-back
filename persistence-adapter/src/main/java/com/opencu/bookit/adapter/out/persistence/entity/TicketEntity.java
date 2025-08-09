package com.opencu.bookit.adapter.out.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencu.bookit.domain.model.ticket.TicketPriority;
import com.opencu.bookit.domain.model.ticket.TicketStatus;
import com.opencu.bookit.domain.model.ticket.TicketType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TICKETS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    @JsonIgnore
    private AreaEntity areaEntity;

    @Column(nullable = false)
    private TicketType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    /**Reason of holding or rejecting the ticket. In other cases is null*/
    @Column
    private String reason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    /** First response time (transition to IN_PROGRESS). */
    @Column
    private LocalDateTime firstRespondedAt;

    /** Solution time (transition to RESOLVED). */
    @Column
    private LocalDateTime resolvedAt;

    /** Closing time (transition to CLOSED). */
    @Column
    private LocalDateTime closedAt;
}