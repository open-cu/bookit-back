package ru.tbank.bookit.book_it_backend.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import ru.tbank.bookit.book_it_backend.DTO.EventResponse;
import ru.tbank.bookit.book_it_backend.mapper.EventMapper;
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
    private final EventMapper eventMapper;

    public EventService(EventRepository eventRepository, UserRepository userRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
    }

    public Optional<Event> findById(UUID eventId) {
        return eventRepository.findById(eventId);
    }

    public List<EventResponse> findAll() {
        return eventMapper.toEventResponseList(eventRepository.findAll());
    }

    public List<EventResponse> toEventResponseList(List<Event> events) {
        return eventMapper.toEventResponseList(events);
    }

    public EventResponse toEventResponse(Event event) {
        return eventMapper.toEventResponse(event);
    }

    public List<EventResponse> findByTags(Set<ThemeTags> tags){
        return eventMapper.toEventResponseList(eventRepository.findByTagsIn(tags));
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

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }
}
