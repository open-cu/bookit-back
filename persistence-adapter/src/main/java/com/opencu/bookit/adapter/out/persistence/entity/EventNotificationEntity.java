package com.opencu.bookit.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "event_notifications")
public class EventNotificationEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "event_title")
    private String eventTitle;

    @Column(name = "event_date_time")
    private LocalDateTime eventDateTime;

    @Column(name = "message")
    private String message;
}
