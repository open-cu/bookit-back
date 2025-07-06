package ru.tbank.bookit.book_it_backend.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    public Page<EventResponse> findWithFilters(
            Set<ThemeTags> tags, String search, String status, Pageable pageable, UUID currentUserId
    ) {
        Specification<Event> spec = Specification.where(null);

        if (tags != null && !tags.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.join("tags").in(tags));
        }
        if (search != null && !search.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("description")), "%" + search.toLowerCase() + "%")
                    )
            );
        }

        if (status != null && currentUserId != null) {
            Optional<User> userOpt = userRepository.findById(currentUserId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (status.equalsIgnoreCase("registered")) {
                    spec = spec.and((root, query, cb) -> cb.isMember(user, root.get("users")));
                } else if (status.equalsIgnoreCase("available")) {
                    spec = spec.and((root, query, cb) -> cb.and(
                            cb.greaterThan(root.get("available_places"), 0),
                            cb.not(cb.isMember(user, root.get("users")))
                    ));
                }
            }
        }

        Page<Event> eventsPage = eventRepository.findAll(spec, pageable);
        return eventsPage.map(eventMapper::toEventResponse);
    }
}
