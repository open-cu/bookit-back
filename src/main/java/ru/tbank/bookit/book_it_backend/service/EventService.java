package ru.tbank.bookit.book_it_backend.service;

import org.springframework.stereotype.Service;
import ru.tbank.bookit.book_it_backend.model.Event;
import ru.tbank.bookit.book_it_backend.model.EventStatus;
import ru.tbank.bookit.book_it_backend.model.ThemeTags;
import ru.tbank.bookit.book_it_backend.repository.EventRepository;

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
        if (!isIdPresent(userId, event.getUser_list()) && event.getAvailable_places() > 0) {
            event.getUser_list().add(userId);
            event.setAvailable_places(event.getAvailable_places() - 1);
            eventRepository.save(event);
        }
    }

    public boolean isIdPresent(UUID userId, List<UUID> users){
        if (users == null || users.isEmpty()) {
            return false;
        }
        return users.stream().anyMatch(userId::equals);
    }

    public void removeUser(UUID userId, Event event) {
        if (isIdPresent(userId, event.getUser_list())) {
            event.getUser_list().remove(userId);
            event.setAvailable_places(event.getAvailable_places() + 1);
            eventRepository.save(event);
        }
    }
}
