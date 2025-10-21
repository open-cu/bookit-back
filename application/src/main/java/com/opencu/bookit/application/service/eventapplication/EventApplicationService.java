package com.opencu.bookit.application.service.eventapplication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencu.bookit.application.port.in.CreateEventApplicationUseCase;
import com.opencu.bookit.application.port.out.event.LoadEventApplicationPort;
import com.opencu.bookit.application.port.out.event.LoadEventPort;
import com.opencu.bookit.application.port.out.event.SaveEventApplicationPort;
import com.opencu.bookit.application.port.out.user.LoadUserPort;
import com.opencu.bookit.application.service.event.EventService;
import com.opencu.bookit.application.service.nofication.NotificationService;
import com.opencu.bookit.domain.model.event.EventApplicationModel;
import com.opencu.bookit.domain.model.event.EventModel;
import com.opencu.bookit.domain.model.event.EventApplicationStatus;
import com.opencu.bookit.domain.model.event.EventNotification;
import com.opencu.bookit.domain.model.user.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final EventService eventService;
    private final NotificationService notificationService;

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

        if (birthDateFrom != null && birthDateTo != null && birthDateFrom.isAfter(birthDateTo)) {
            throw new IllegalArgumentException("'birthDateFrom' cannot be after 'birthDateTo'.");
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

        if (!loadEventPort.requiresApplication(eventId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event does not require applications.");
        }

        EventModel event = loadEventPort.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found with id: " + eventId));

        UserModel user = loadUserPort.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId));

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
        if (application.getStatus() != EventApplicationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending event applications can be deleted");
        }

        deleteEventApplicationPort.deleteById(applicationId);
    }

    @Transactional
    public void updateStatus(UUID applicationId, EventApplicationStatus status) {
        EventApplicationModel application = loadEventApplicationPort.findById(applicationId).orElseThrow(
                () -> new IllegalArgumentException("Event application not found with id: " + applicationId)
        );

        saveEventApplicationPort.changeStatusById(applicationId, status);

        final UserModel user = application.getUserModel();
        final EventModel event = application.getEventModel();
        switch (status) {
            case PENDING -> throw new IllegalArgumentException("Cannot change, status to PENDING.");
            case APPROVED -> eventService.addUser(user.getId(), event);
            case REJECTED -> {
                eventService.removeUser(user.getId(), event);
                EventNotification eventNotification = new EventNotification(
                        UUID.randomUUID(),
                        user.getId(),
                        user.getEmail(),
                        user.getTgId(),
                        event.getId(),
                        event.getName(),
                        event.getStartTime(),
                        "Ваша заявка на мероприятие: " + event.getName() + " была отклонена."
                );

                notificationService.sendEventNotificationNow(eventNotification);
            }
            default -> throw new IllegalArgumentException("Invalid status: " + status);
        }
    }
}
