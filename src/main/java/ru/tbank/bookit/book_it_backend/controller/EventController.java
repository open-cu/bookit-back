package ru.tbank.bookit.book_it_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.tbank.bookit.book_it_backend.model.Event;
import ru.tbank.bookit.book_it_backend.model.EventStatus;
import ru.tbank.bookit.book_it_backend.model.NewsTag;
import ru.tbank.bookit.book_it_backend.repository.EventRepository;
import ru.tbank.bookit.book_it_backend.service.EventService;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;
    private final EventRepository eventRepository;

    public EventController(EventService eventService, EventRepository eventRepository) {
        this.eventService = eventService;
        this.eventRepository = eventRepository;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> event = eventService.findAll();
        return ResponseEntity.ok(event);
    }

    @GetMapping("/by-tags")
    public ResponseEntity<List<Event>> getAllEventsByTags(
            @RequestParam(required = true) Set<NewsTag> tags) {
        List<Event> event = eventService.findByTags(tags);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/status")
    public ResponseEntity<EventStatus> getStatusById(@RequestParam String userId, @RequestParam String eventId){
        Event event = eventService.findById(eventId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        return ResponseEntity.ok(eventService.findStatusById(userId, event));
    }

    @PutMapping("/register")
    public ResponseEntity<Event> addUserInList(@RequestParam String userId, @RequestParam String eventId){
        Event event = eventService.findById(eventId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        eventService.addUser(userId, event);
        return ResponseEntity.ok(event);
    }

    @PostMapping("/event")
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        eventRepository.save(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }
}