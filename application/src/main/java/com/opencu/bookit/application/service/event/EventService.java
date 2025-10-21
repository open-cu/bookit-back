package com.opencu.bookit.application.service.event;

import com.opencu.bookit.application.port.in.booking.CRUDBookingUseCase;
import com.opencu.bookit.application.port.out.area.LoadAreaPort;
import com.opencu.bookit.application.port.out.event.DeleteEventPort;
import com.opencu.bookit.application.port.out.event.LoadEventApplicationPort;
import com.opencu.bookit.application.port.out.event.LoadEventPort;
import com.opencu.bookit.application.port.out.event.SaveEventPort;
import com.opencu.bookit.application.port.out.user.LoadUserPort;
import com.opencu.bookit.application.service.booking.BookingService;
import com.opencu.bookit.application.service.nofication.NotificationService;
import com.opencu.bookit.domain.model.booking.ValidationRule;
import com.opencu.bookit.domain.model.contentcategory.*;
import com.opencu.bookit.domain.model.event.*;
import com.opencu.bookit.domain.model.user.UserModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private final BookingService bookingService;
    private final LoadAreaPort loadAreaPort;
    private final LoadEventApplicationPort loadEventApplicationPort;

    @Value("${booking.zone-id}")
    private ZoneId zoneId;

    @Value("${notification.default-time-before-event-in-days}")
    private int defaultTimeBeforeEventInDays;

    public EventService(LoadEventPort loadEventPort, SaveEventPort saveEventPort,
                        LoadUserPort loadUserPort, DeleteEventPort deleteEventPort, LoadAreaPort loadAreaPort,
                        NotificationService notificationService, BookingService bookingService,
                        LoadEventApplicationPort loadEventApplicationPort1) {
        this.loadEventPort = loadEventPort;
        this.saveEventPort = saveEventPort;
        this.loadUserPort = loadUserPort;
        this.deleteEventPort = deleteEventPort;
        this.notificationService = notificationService;
        this.bookingService = bookingService;
        this.loadAreaPort = loadAreaPort;
        this.loadEventApplicationPort = loadEventApplicationPort1;
    }

    public Optional<EventModel> findById(UUID eventId) {
        return loadEventPort.findById(eventId);
    }

    public List<EventModel> findAll() {
        return loadEventPort.findAll();
    }

    public List<EventModel> findByTags(Set<ThemeTags> tags) {
        return loadEventPort.findByTags(tags);
    }

    public EventStatus findStatusById(UUID userId, EventModel eventModel){
        if (eventModel.getEndTime().isBefore(LocalDateTime.now(zoneId))) {
            return EventStatus.COMPLETED;
        } else if (isUserPresent(userId, eventModel)) {
            return EventStatus.REGISTERED;
        } else if (eventModel.getAvailable_places() <= 0) {
            return EventStatus.FULL;
        } else if (eventModel.isRequiresApplication()) {
            Optional<EventApplicationModel> application = loadEventApplicationPort.findByUserIdAndEventId(userId, eventModel.getId());
            return application.map(eventApplicationModel -> eventApplicationModel.getStatus() == EventApplicationStatus.APPROVED ?
                    EventStatus.AVAILABLE : EventStatus.APPLICATION_SENT).orElse(EventStatus.AVAILABLE_FOR_APPLICATION);
        } else {
            return EventStatus.AVAILABLE;
        }
    }

    public void addUser(UUID userId, EventModel eventModel) {
        if (eventModel.isRequiresApplication()) {
            Optional<EventApplicationModel> application = loadEventApplicationPort.findByUserIdAndEventId(userId, eventModel.getId());

            if (application.isEmpty()) {
                throw new IllegalArgumentException("User " + userId + " has already submitted an application for this event");
            }

            if (application.get().getStatus() != EventApplicationStatus.APPROVED) {
                throw new IllegalArgumentException("User " + userId + " application for this event is not approved");
            }
        }

        UserModel userModel = loadUserPort.findById(userId).orElseThrow(() -> new NoSuchElementException("User " + userId + " not found"));
        if (eventModel.getUserModels().contains(userModel)) {
            throw new IllegalArgumentException("User " + userModel.getId() + " is already registered for this event");
        }
        if (eventModel.getAvailable_places() <= 0) {
            throw new IllegalArgumentException("There are no available places for this event " + eventModel.getId());
        }
        if (eventModel.getStartTime().isBefore(LocalDateTime.now(zoneId))) {
            throw new IllegalArgumentException("Cannot register for an event "  + eventModel.getId() + " that has already occurred");
        }
        eventModel.getUserModels().add(userModel);
        eventModel.setAvailable_places(eventModel.getAvailable_places() - 1);
        saveEventPort.save(eventModel);

        CRUDBookingUseCase.CreateBookingCommand createBookingCommand = new CRUDBookingUseCase.CreateBookingCommand(
                userId,
                eventModel.getAreaModel().getId(),
                Optional.of(eventModel.getId()),
                Set.of(Pair.of(eventModel.getStartTime(), eventModel.getEndTime())),
                1
        );

        Set<ValidationRule> rulesToApply = Set.of(ValidationRule.VALIDATE_TIME_RESTRICTIONS);
        bookingService.createBooking(createBookingCommand, rulesToApply);

        EventNotification eventNotification = new EventNotification(
                UUID.randomUUID(),
                userId,
                userModel.getEmail(),
                userModel.getTgId(),
                eventModel.getId(),
                eventModel.getName(),
                eventModel.getStartTime(),
                "Вы успешно зарегистрированы на мероприятие: " + eventModel.getName()
        );

        if (eventModel.getStartTime().isBefore(LocalDateTime.now(zoneId).plusDays(defaultTimeBeforeEventInDays))) {
            notificationService.sendEventNotificationNow(eventNotification);
        }
        else {
            notificationService.scheduleEventNotification(eventNotification,
                    eventModel.getStartTime().minusDays(defaultTimeBeforeEventInDays));
        }
    }

    public boolean isUserPresent(UUID userId, EventModel eventModel) {
        return eventModel.getUserModels().stream().anyMatch(u -> u.getId().equals(userId));
    }
    @Transactional
    public void removeUser(UUID userId, EventModel eventModel) {
        UserModel userModel = loadUserPort.findById(userId)
                                          .orElseThrow(() -> new NoSuchElementException("User " + userId + " not found"));
        if (eventModel.getUserModels().remove(userModel)) {
            eventModel.setAvailable_places(eventModel.getAvailable_places() + 1);
            saveEventPort.save(eventModel);
            bookingService.deleteBookingAccordingToIndirectParameters(userId, eventModel.getAreaModel().getId(),
                    eventModel.getStartTime(), eventModel.getEndTime());
            notificationService.cancelNotification(userId, eventModel.getId());
        }
    }

    public EventModel saveEvent(EventModel event) {
        return saveEventPort.save(event);
    }

    @Transactional
    public EventModel updateEvent(
            UUID eventId,
            String name,
            String shortDescription,
            String fullDescription,
            List<ThemeTags> tags,
            List<ContentFormat> formats,
            List<ContentTime> times,
            List<ParticipationFormat> participationFormats,
            List<TargetAudience> targetAudiences,
            List<String> keys,
            LocalDateTime startTime,
            LocalDateTime endTime,
            int availablePlaces,
            UUID areaId
    ) {
        Optional<EventModel> eventOpt = loadEventPort.findById(eventId);
        if (eventOpt.isEmpty()) {
            throw new NoSuchElementException("No such event " + eventId + " found");
        }
        EventModel eventModel = eventOpt.get();
        setEventModelValues(name, shortDescription, fullDescription, tags, formats, times, participationFormats, targetAudiences, keys, startTime, eventModel);
        eventModel.setAvailable_places(availablePlaces);
        eventModel.setEndTime(endTime);
        eventModel.setAreaModel(loadAreaPort.findById(areaId)
                .orElseThrow(() -> new NoSuchElementException("No such area " + areaId + " found")));

        EventModel event = loadEventPort.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("No such event " + eventId + " found"));
        Set<UserModel> users = event.getUserModels();

        CRUDBookingUseCase.UpdateBookingQuery updateBookingQuery = new CRUDBookingUseCase.UpdateBookingQuery(
                event.getAreaModel().getId(),
                event.getStartTime(),
                event.getEndTime()
        );
        Set<ValidationRule> rulesToApply = Set.of(ValidationRule.VALIDATE_TIME_RESTRICTIONS, ValidationRule.VALIDATE_AREA_AVAILABILITY);
        bookingService.updateBooking(eventModel.getSystemBooking().getId(), updateBookingQuery, rulesToApply);
        for (UserModel user : users) {
            bookingService.updateBookingAccordingToIndirectParameters(updateBookingQuery, Set.of(), user.getId(), event.getAreaModel().getId(),
                    event.getStartTime(), event.getEndTime());
            notificationService.cancelNotification(user.getId(), eventId);
            EventNotification eventNotification = new EventNotification(
                    UUID.randomUUID(),
                    user.getId(),
                    user.getEmail(),
                    user.getTgId(),
                    eventModel.getId(),
                    eventModel.getName(),
                    eventModel.getStartTime(),
                    "Изменения в проведении мероприятия: " + eventModel.getName() +
                            ". Новое время: " + eventModel.getStartTime() +
                            ". Новое место: " + eventModel.getAreaModel().getName()
            );
            notificationService.sendEventNotificationNow(eventNotification);
        }

        return saveEventPort.save(eventModel);
    }

    private void setEventModelValues(String name, String shortDescription, String fullDescription, List<ThemeTags> tags, List<ContentFormat> formats, List<ContentTime> times, List<ParticipationFormat> participationFormats, List<TargetAudience> targetAudiences, List<String> keys, LocalDateTime startTime, EventModel eventModel) {
        eventModel.setName(name);
        eventModel.setShortDescription(shortDescription);
        eventModel.setFullDescription(fullDescription);
        eventModel.setTags(new HashSet<>(tags));
        eventModel.setFormats(new HashSet<>(formats));
        eventModel.setTimes(new HashSet<>(times));
        eventModel.setParticipationFormats(new HashSet<>(participationFormats));
        eventModel.setTargetAudiences(new HashSet<>(targetAudiences));
        eventModel.setKeys(keys);
        eventModel.setStartTime(startTime);
    }

    public Page<EventModel> findWithFilters(
            LocalDate startDate, LocalDate endDate,
            Set<ThemeTags> tags, Set<ContentFormat> formats, Set<ContentTime> times,
            Set<ParticipationFormat> participationFormats, Set<TargetAudience> targetAudiences,
            String search, String status, Pageable pageable, UUID currentUserId
    ) {
        return loadEventPort.findWithFilters(startDate, endDate, tags, formats, times, participationFormats, targetAudiences,
                search, status, pageable, currentUserId);
    }

    @Transactional
    public void deleteById(UUID eventId) {
        EventModel event = loadEventPort.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("No such event " + eventId + " found"));

        bookingService.deleteById(event.getSystemBooking().getId());

        Set<UserModel> users = event.getUserModels();
        for (UserModel user : users) {
            bookingService.deleteBookingAccordingToIndirectParameters(user.getId(), event.getAreaModel().getId(),
                    event.getStartTime(), event.getEndTime());
            notificationService.cancelNotification(user.getId(), eventId);
            EventNotification eventNotification = new EventNotification(
                    UUID.randomUUID(),
                    user.getId(),
                    user.getEmail(),
                    user.getTgId(),
                    event.getId(),
                    event.getName(),
                    event.getStartTime(),
                    "Отменено мероприятие: " + event.getName()
            );
            notificationService.sendEventNotificationNow(eventNotification);
        }
        deleteEventPort.delete(eventId);
    }

    @Transactional
    public EventModel createEvent(
            String name,
            String shortDescription,
            String fullDescription,
            List<ThemeTags> tags,
            List<ContentFormat> formats,
            List<ContentTime> times,
            List<ParticipationFormat> participationFormats,
            List<TargetAudience> targetAudiences,
            List<String> keys,
            LocalDateTime startTime,
            LocalDateTime endTime,
            int availablePlaces,
            UUID areaId
    ) {
        EventModel eventModel = new EventModel();
        setEventModelValues(name, shortDescription, fullDescription, tags, formats, times, participationFormats, targetAudiences, keys, startTime, eventModel);
        eventModel.setEndTime(endTime);
        eventModel.setAvailable_places(availablePlaces);
        eventModel.setAreaModel(loadAreaPort.findById(areaId)
                .orElseThrow(() -> new NoSuchElementException("No such area " + areaId + " found")));

        CRUDBookingUseCase.CreateBookingCommand createBookingCommand = new CRUDBookingUseCase.CreateBookingCommand(
                loadUserPort.getSystemUser().getId(),
                eventModel.getAreaModel().getId(),
                Optional.empty(),
                Set.of(Pair.of(eventModel.getStartTime(), eventModel.getEndTime())),
                0
        );

        Set<ValidationRule> rulesToApply = Set.of(ValidationRule.VALIDATE_TIME_RESTRICTIONS, ValidationRule.VALIDATE_AREA_AVAILABILITY);
        eventModel.setSystemBooking(bookingService.createBooking(createBookingCommand, rulesToApply).getFirst());
        return saveEventPort.save(eventModel);
    }
}
