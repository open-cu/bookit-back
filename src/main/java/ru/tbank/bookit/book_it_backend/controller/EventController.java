package ru.tbank.bookit.book_it_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @Operation(
            description = "Returns information in the list format about all events"
    )
    @GetMapping("/all")
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> event = eventService.findAll();
        return ResponseEntity.ok(event);
    }

    @Operation(
            description = "Returns information in list format about all events for a specific tag"
    )
    @GetMapping("/by-tags")
    public ResponseEntity<List<Event>> getAllEventsByTags(
            @RequestParam(required = true) Set<NewsTag> tags) {
        List<Event> event = eventService.findByTags(tags);
        return ResponseEntity.ok(event);
    }

    @Operation(
            description = "Returns information about the event status by user"
    )
    @GetMapping("/status")
    public ResponseEntity<EventStatus> getStatusById(@RequestParam long userId, @RequestParam long eventId){
        Event event = eventService.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        return ResponseEntity.ok(eventService.findStatusById(userId, event));
    }

    @Operation(
            description = "Registers (by entering the guest list of the event) the user for this event"
    )
    @PutMapping("/register")
    public ResponseEntity<Event> addUserInList(@RequestParam long userId, @RequestParam long eventId){
        Event event = eventService.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        eventService.addUser(userId, event);
        return ResponseEntity.ok(event);
    }

    @Operation(
            description = "returns information about the created event"
    )
    @PostMapping("/event")
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        eventRepository.save(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }
}