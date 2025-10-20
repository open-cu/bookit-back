package com.opencu.bookit.application.service.eventapplication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencu.bookit.application.port.in.CreateEventApplicationUseCase;
import com.opencu.bookit.application.port.out.event.LoadEventApplicationPort;
import com.opencu.bookit.application.port.out.event.LoadEventPort;
import com.opencu.bookit.application.port.out.event.SaveEventApplicationPort;
import com.opencu.bookit.application.port.out.user.LoadUserPort;
import com.opencu.bookit.domain.model.event.EventApplicationModel;
import com.opencu.bookit.domain.model.event.EventModel;
import com.opencu.bookit.domain.model.event.EventApplicationStatus;
import com.opencu.bookit.domain.model.user.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventApplicationService implements CreateEventApplicationUseCase {
    private final LoadEventApplicationPort loadEventApplicationPort;
    private final SaveEventApplicationPort saveEventApplicationPort;
    private final LoadUserPort loadUserPort;
    private final LoadEventPort loadEventPort;
    private final ObjectMapper objectMapper;
    private final com.opencu.bookit.application.port.out.event.DeleteEventApplicationPort deleteEventApplicationPort;

    public Page<EventApplicationModel> findWithFilters(
            UUID userId,
            UUID eventId,
            LocalDate birthDateFrom,
            LocalDate birthDateTo,
            String city,
            String details,
            EventApplicationStatus status,
            Pageable pageable
    ) {
        JsonNode detailsJson = null;
        if (details != null && !details.isEmpty()) {
            try {
                detailsJson = objectMapper.readTree(details);
            } catch (IOException e) {
                throw new IllegalArgumentException("Invalid JSON format for 'details' parameter.", e);
            }
        }

        LoadEventApplicationPort.EventApplicationFilter filter = new LoadEventApplicationPort.EventApplicationFilter(
                userId, eventId, birthDateFrom, birthDateTo, city, detailsJson, status
        );

        return loadEventApplicationPort.findWithFilters(filter, pageable);
    }

    @Override
    public EventApplicationModel createEventApplication(UUID eventId, UUID userId, String cityOfResidence, LocalDate dateOfBirth, JsonNode activityDetails) {
        loadEventApplicationPort.findByUserIdAndEventId(userId, eventId).ifPresent(app -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Event application for this event by this user already exists.");
        });

        UserModel user = loadUserPort.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId));

        EventModel event = loadEventPort.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found with id: " + eventId));

        EventApplicationModel newApplication = new EventApplicationModel();
        newApplication.setUserModel(user);
        newApplication.setEventModel(event);
        newApplication.setCityOfResidence(cityOfResidence);
        newApplication.setDateOfBirth(dateOfBirth);
        newApplication.setActivityDetails(activityDetails);

        return saveEventApplicationPort.save(newApplication);
    }

    public void deleteByUser(UUID applicationId, UUID currentUserId) {
        EventApplicationModel application = loadEventApplicationPort.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event application not found with id: " + applicationId));

        if (application.getUserModel() == null || application.getUserModel().getId() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Event application has no associated user");
        }
        if (!application.getUserModel().getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can delete only your own event application");
        }

        deleteEventApplicationPort.deleteById(applicationId);
    }
}
