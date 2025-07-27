package com.opencu.bookit.application.service.event;

import com.opencu.bookit.application.port.out.event.DeleteEventPort;
import com.opencu.bookit.application.port.out.event.LoadEventPort;
import com.opencu.bookit.application.port.out.event.SaveEventPort;
import com.opencu.bookit.application.port.out.user.LoadUserPort;
import com.opencu.bookit.application.service.nofication.NotificationService;
import com.opencu.bookit.domain.model.contentcategory.ContentFormat;
import com.opencu.bookit.domain.model.contentcategory.ContentTime;
import com.opencu.bookit.domain.model.contentcategory.ParticipationFormat;
import com.opencu.bookit.domain.model.event.EventModel;
import com.opencu.bookit.domain.model.event.EventNotification;
import com.opencu.bookit.domain.model.event.EventStatus;
import com.opencu.bookit.domain.model.contentcategory.ThemeTags;
import com.opencu.bookit.domain.model.user.UserModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class EventService {
    private final LoadEventPort loadEventPort;
    private final SaveEventPort saveEventPort;
    private final LoadUserPort loadUserPort;
    private final DeleteEventPort deleteEventPort;
    private final NotificationService notificationService;

    @Value("${booking.zone-id}")
    private ZoneId zoneId;

    @Value("${notification.default-time-before-event-in-days}")
    private int defaultTimeBeforeEventInDays;

    public EventService(LoadEventPort loadEventPort, SaveEventPort saveEventPort, LoadUserPort loadUserPort, DeleteEventPort deleteEventPort, NotificationService notificationService) {
        this.loadEventPort = loadEventPort;
        this.saveEventPort = saveEventPort;
        this.loadUserPort = loadUserPort;
        this.deleteEventPort = deleteEventPort;
        this.notificationService = notificationService;
    }

    public Optional<EventModel> findById(UUID eventId) {
        return loadEventPort.findById(eventId);
    }

    public List<EventModel> findAll() {
        return loadEventPort.findAll();
    }

    public List<EventModel> findByTags(Set<ThemeTags> tags){
        return loadEventPort.findByTags(tags);
    }

    public EventStatus findStatusById(UUID userId, EventModel eventModel){
        if (isUserPresent(userId, eventModel)) {
            return EventStatus.REGISTERED;
        } else if (eventModel.getAvailable_places() > 0) {
            return EventStatus.AVAILABLE;
        } else {
            return EventStatus.FULL;
        }
    }

    public void addUser(UUID userId, EventModel eventModel) {
        UserModel userModel = loadUserPort.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));
        if (eventModel.getUserModels().contains(userModel)) {
            throw new IllegalArgumentException("User is already registered for this event");
        }
        if (eventModel.getAvailable_places() <= 0) {
            throw new IllegalArgumentException("There are no available places for this event");
        }
        if (eventModel.getDate().isBefore(LocalDateTime.now(zoneId))) {
            throw new IllegalArgumentException("Cannot register for an event that has already occurred");
        }
        eventModel.getUserModels().add(userModel);
        eventModel.setAvailable_places(eventModel.getAvailable_places() - 1);
        saveEventPort.save(eventModel);


        EventNotification eventNotification = new EventNotification(
                UUID.randomUUID(),
                userId,
                userModel.getEmail(),
                eventModel.getId(),
                eventModel.getName(),
                eventModel.getDate(),
                "Вы успешно зарегистрированы на мероприятие: " + eventModel.getName()
        );

        if (eventModel.getDate().isBefore(LocalDateTime.now(zoneId).plusDays(defaultTimeBeforeEventInDays))) {
            notificationService.sendEventNotificationNow(eventNotification);
        }
        else {
            notificationService.scheduleEventNotification(eventNotification,
                    eventModel.getDate().minusDays(defaultTimeBeforeEventInDays));
        }

    }

    public boolean isUserPresent(UUID userId, EventModel eventModel) {
        return eventModel.getUserModels().stream().anyMatch(u -> u.getId().equals(userId));
    }

    public void removeUser(UUID userId, EventModel eventModel) {
        UserModel userModel = loadUserPort.findById(userId)
                                          .orElseThrow(() -> new NoSuchElementException("User not found"));
        if (eventModel.getUserModels().remove(userModel)) {
            eventModel.setAvailable_places(eventModel.getAvailable_places() + 1);
            saveEventPort.save(eventModel);
        }
    }

    public EventModel saveEvent(EventModel event) {
        return saveEventPort.save(event);
    }

    @Transactional
    public EventModel updateEvent(
            UUID eventId,
            String name,
            String description,
            List<ThemeTags> tags,
            List<ContentFormat> formats,
            List<ContentTime> times,
            List<ParticipationFormat> participationFormats,
            List<String> keys,
            LocalDateTime date,
            int availablePlaces
    ) {
        Optional<EventModel> eventOpt = loadEventPort.findById(eventId);
        if (eventOpt.isEmpty()) {
            throw new NoSuchElementException("No such event found");
        }
        EventModel eventModel = eventOpt.get();
        eventModel.setName(name);
        eventModel.setDescription(description);
        eventModel.setTags(new HashSet<>(tags));
        eventModel.setFormats(new HashSet<>(formats));
        eventModel.setTimes(new HashSet<>(times));
        eventModel.setParticipationFormats(new HashSet<>(participationFormats));
        eventModel.setKeys(keys);
        eventModel.setDate(date);
        eventModel.setAvailable_places(availablePlaces);
        return saveEventPort.save(eventModel);
    }

    public Page<EventModel> findWithFilters(
            Set<ThemeTags> tags, Set<ContentFormat> formats, Set<ContentTime> times,
            Set<ParticipationFormat> participationFormats,
            String search, String status, Pageable pageable, UUID currentUserId
    ) {
        return loadEventPort.findWithFilters(tags, formats, times, participationFormats,
                search, status, pageable, currentUserId);
    }

    @Transactional
    public void deleteById(UUID eventId) {
        deleteEventPort.delete(eventId);
    }

    @Transactional
    public EventModel createEvent(
            String name,
            String description,
            List<ThemeTags> tags,
            List<ContentFormat> formats,
            List<ContentTime> times,
            List<ParticipationFormat> participationFormats,
            List<String> keys,
            LocalDateTime date,
            int availablePlaces
    ) {
        EventModel eventModel = new EventModel();
        eventModel.setName(name);
        eventModel.setDescription(description);
        eventModel.setTags(new HashSet<>(tags));
        eventModel.setFormats(new HashSet<>(formats));
        eventModel.setTimes(new HashSet<>(times));
        eventModel.setParticipationFormats(new HashSet<>(participationFormats));
        eventModel.setKeys(keys);
        eventModel.setDate(date);
        eventModel.setAvailable_places(availablePlaces);
        return saveEventPort.save(eventModel);
    }
}
