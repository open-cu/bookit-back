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

    public Integer findAvailablePlaces(Event event) {
        return event.getAvailable_places();
    }

    public void removeUser(UUID userId, Event event) {
        String pastString = event.getUser_list();
        String finalString = Arrays.stream(pastString.split("\\s+"))
                .filter(part -> !part.equals(userId.toString())).collect(Collectors.joining(" "));
        event.setUser_list(finalString);
        event.setAvailable_places(event.getAvailable_places() + 1);
    }
}
