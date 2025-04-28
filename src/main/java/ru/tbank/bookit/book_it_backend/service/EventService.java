package ru.tbank.bookit.book_it_backend.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import ru.tbank.bookit.book_it_backend.model.Event;
import ru.tbank.bookit.book_it_backend.model.EventStatus;
import ru.tbank.bookit.book_it_backend.model.ThemeTags;
import ru.tbank.bookit.book_it_backend.model.User;
import ru.tbank.bookit.book_it_backend.repository.EventRepository;
import ru.tbank.bookit.book_it_backend.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
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
        if (isUserPresent(userId, event)) {
            return EventStatus.REGISTERED;
        } else if (event.getAvailable_places() > 0) {
            return EventStatus.AVAILABLE;
        } else {
            return EventStatus.FULL;
        }
    }

    public void addUser(UUID userId, Event event) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (!event.getUsers().contains(user) && event.getAvailable_places() > 0) {
            event.getUsers().add(user);
            event.setAvailable_places(event.getAvailable_places() - 1);
            eventRepository.save(event);
        }
    }

    public boolean isUserPresent(UUID userId, Event event) {
        return event.getUsers().stream().anyMatch(u -> u.getId().equals(userId));
    }

    public void removeUser(UUID userId, Event event) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (event.getUsers().remove(user)) {
            event.setAvailable_places(event.getAvailable_places() + 1);
            eventRepository.save(event);
        }
    }
}
