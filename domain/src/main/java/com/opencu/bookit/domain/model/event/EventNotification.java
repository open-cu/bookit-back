package com.opencu.bookit.domain.model.event;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EventNotification {
    private UUID id;
    private UUID userId;
    private String userEmail;
    private UUID eventId;
    private String eventTitle;
    private LocalDateTime eventDateTime;
    private String message;
}