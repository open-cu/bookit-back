package ru.tbank.bookit.book_it_backend.service;
import java.util.function.Consumer;
import java.util.Objects;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.bookit.book_it_backend.DTO.CreateEventRequest;
import ru.tbank.bookit.book_it_backend.model.Event;
import ru.tbank.bookit.book_it_backend.model.EventStatus;
import ru.tbank.bookit.book_it_backend.model.ThemeTags;
import ru.tbank.bookit.book_it_backend.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Optional<Event> findById(UUID eventId) {
        return eventRepository.findById(eventId);
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public List<Event> findByTags(Set<ThemeTags> tags){
        return eventRepository.findByTagsIn(tags);
    }

    public void deleteEvent(UUID eventId) {
        eventRepository.deleteById(eventId);
    }

    public EventStatus findStatusById(UUID userId, Event event){
        if (isIdPresent(userId, event.getUser_list())) {
            return EventStatus.REGISTERED;
        } else if (event.getAvailable_places() > 0) {
            return EventStatus.AVAILABLE;
        } else {
            return EventStatus.FULL;
        }
    }

    public void addUser(UUID userId, Event event){
        String userList = event.getUser_list();
        if (!isIdPresent(userId, userList) && event.getAvailable_places() > 0) {
            if (userList == null) {
                userList = "";
            }
            event.setUser_list(userList + userId + " ");
            event.setAvailable_places(event.getAvailable_places() - 1);
            eventRepository.save(event);
        }
    }

    public boolean isIdPresent(UUID userId, String users){
        if (users == null || users.isEmpty()) {
            return false;
        }
        Set<UUID> uuids = Arrays.stream(users.split(" ")).filter(str -> !str.isEmpty()).map(UUID::fromString).collect(Collectors.toSet());
        return uuids.contains(userId);
    }

    public void removeUser(UUID userId, Event event) {
        String pastString = event.getUser_list();
        String finalString = Arrays.stream(pastString.split("\\s+"))
                .filter(part -> !part.equals(userId.toString())).collect(Collectors.joining(" "));
        event.setUser_list(finalString);
        event.setAvailable_places(event.getAvailable_places() + 1);
        eventRepository.save(event);
    }

    @Transactional
    public Event createEvent(CreateEventRequest request) {
        if (request.getDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Event date must be in the future");
        }

        if (request.getAvailable_places() <= 0) {
            throw new IllegalStateException("Available places must be greater than 0");
        }

        Event event = new Event();
        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setTags(request.getTags());
        event.setDate(request.getDate());
        event.setAvailable_places(request.getAvailable_places());
        event.setUser_list("");

        return eventRepository.save(event);
    }

    @Transactional
    public Event updateEvent(UUID eventId, String name, String description,
                             Set<ThemeTags> tags, LocalDateTime date,
                             int availablePlaces) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));

        validateEventUpdate(event, date);

        updateIfChanged(event::setName, event.getName(), name);
        updateIfChanged(event::setDescription, event.getDescription(), description);
        updateIfChanged(event::setTags, event.getTags(), tags);
        updateIfChanged(event::setDate, event.getDate(), date);
        updateIfChanged(event::setAvailable_places, event.getAvailable_places(), availablePlaces);

        return eventRepository.save(event);
    }

    private <T> void updateIfChanged(Consumer<T> setter, T oldValue, T newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            setter.accept(newValue);
        }
    }

    private void validateEventUpdate(Event event, LocalDateTime newDate) {
        if (event.getDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot update past event");
        }
        if (newDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("New event date must be in the future");
        }
    }

}
