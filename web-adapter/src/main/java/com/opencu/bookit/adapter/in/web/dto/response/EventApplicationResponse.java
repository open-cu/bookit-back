package com.opencu.bookit.adapter.in.web.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.opencu.bookit.domain.model.event.EventApplicationStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class EventApplicationResponse {
    private UUID id;
    private UUID userId;
    private UUID eventId;
    private String cityOfResidence;
    private LocalDate dateOfBirth;
    private JsonNode activityDetails;
    private EventApplicationStatus status;
    private LocalDateTime createdAt;
}
