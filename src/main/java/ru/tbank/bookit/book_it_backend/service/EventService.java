package ru.tbank.bookit.book_it_backend.service;

import org.springframework.stereotype.Service;
import ru.tbank.bookit.book_it_backend.model.Event;
import ru.tbank.bookit.book_it_backend.model.EventStatus;
import ru.tbank.bookit.book_it_backend.model.ThemeTags;
import ru.tbank.bookit.book_it_backend.repository.EventRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Optional<Event> findById(String eventId) {
        return eventRepository.findById(eventId);
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public List<Event> findByTags(Set<ThemeTags> tags){
        return eventRepository.findByTagsIn(tags);
    }

    public EventStatus findStatusById(String userId, Event event){
        if (isIdPresent(userId, event.getUser_list())) {
            return EventStatus.REGISTERED;
        } else if (event.getAvailable_places() > 0) {
            return EventStatus.AVALIABLE;
        } else {
            return EventStatus.FULL;
        }
    }

    public void addUser(String userId, Event event){
        if (!isIdPresent(userId, event.getUser_list()) && event.getAvailable_places() > 0) {
            event.setUser_list(event.getUser_list() + " " + userId);
            event.setAvailable_places(event.getAvailable_places() - 1);
            eventRepository.save(event);
        }
    }

    public boolean isIdPresent(String userId, String users){
        String[] lines = users.split(" ");
        for (String line : lines) {
            String registeredId = line.trim();
            if (!registeredId.isEmpty() && registeredId.equals(userId)) {
                return true;
            }
        }
        return false;
    }
}
