package com.opencu.bookit.domain.model.event;

import com.opencu.bookit.domain.model.user.UserModel;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventApplicationModel {
    private UUID id;
    private UserModel userModel;
    private EventModel eventModel;
    private String cityOfResidence;
    private LocalDate dateOfBirth;
    private JsonNode activityDetails;
    private EventApplicationStatus status = EventApplicationStatus.PENDING;
    private LocalDateTime createdAt;
}