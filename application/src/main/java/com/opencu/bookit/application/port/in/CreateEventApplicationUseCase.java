package com.opencu.bookit.application.port.in;

import com.fasterxml.jackson.databind.JsonNode;
import com.opencu.bookit.domain.model.event.EventApplicationModel;

import java.time.LocalDate;
import java.util.UUID;

public interface CreateEventApplicationUseCase {
    EventApplicationModel createEventApplication(UUID eventId, UUID userId, String cityOfResidence, LocalDate dateOfBirth, JsonNode activityDetails);
}

