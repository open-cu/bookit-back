package com.opencu.bookit.adapter.out.persistence.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.opencu.bookit.domain.model.event.EventApplicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "EVENT_APPLICATIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventApplicationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private EventEntity event;

    @Column(name = "city_of_residence", nullable = false)
    private String cityOfResidence;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "activity_details", columnDefinition = "jsonb", nullable = false)
    private JsonNode activityDetails;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventApplicationStatus status = EventApplicationStatus.PENDING;
}
