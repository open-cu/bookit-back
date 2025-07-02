package com.opencu.bookit.application.event.service;

import com.opencu.bookit.application.event.port.out.LoadEventPort;
import com.opencu.bookit.application.event.port.out.SaveEventPort;
import com.opencu.bookit.application.user.port.out.LoadUserPort;
import com.opencu.bookit.domain.model.event.Event;
import com.opencu.bookit.domain.model.event.EventStatus;
import com.opencu.bookit.domain.model.event.ThemeTags;
import com.opencu.bookit.domain.model.user.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EventService {
    private final LoadEventPort loadEventPort;
    private final SaveEventPort saveEventPort;
    private final LoadUserPort loadUserPort;

    public EventService(LoadEventPort loadEventPort, SaveEventPort saveEventPort, LoadUserPort loadUserPort) {
        this.loadEventPort = loadEventPort;
        this.saveEventPort = saveEventPort;
        this.loadUserPort = loadUserPort;
    }

    public Optional<Event> findById(UUID eventId) {
        return loadEventPort.findById(eventId);
    }

    public List<Event> findAll() {
        //return eventMapper.toEventResponseList(loadEventPort.findAll());
        return loadEventPort.findAll();
    }
/*
    public List<Event> toEventResponseList(List<Event> events) {
        return eventMapper.toEventResponseList(events);
    }

    public EventResponse toEventResponse(Event event) {
        return eventMapper.toEventResponse(event);
    }
*/
    public List<Event> findByTags(Set<ThemeTags> tags){
        return loadEventPort.findByTags(tags);
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
        User user = loadUserPort.findById(userId)
                                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (!event.getUsers().contains(user) && event.getAvailable_places() > 0) {
            event.getUsers().add(user);
            event.setAvailable_places(event.getAvailable_places() - 1);
            saveEventPort.save(event);
        }
    }

    public boolean isUserPresent(UUID userId, Event event) {
        return event.getUsers().stream().anyMatch(u -> u.getId().equals(userId));
    }

    public void removeUser(UUID userId, Event event) {
        User user = loadUserPort.findById(userId)
                                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (event.getUsers().remove(user)) {
            event.setAvailable_places(event.getAvailable_places() + 1);
            saveEventPort.save(event);
        }
    }
}
