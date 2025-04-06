package ru.tbank.bookit.book_it_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.tbank.bookit.book_it_backend.model.Event;
import ru.tbank.bookit.book_it_backend.model.EventStatus;
import ru.tbank.bookit.book_it_backend.model.NewsTag;
import ru.tbank.bookit.book_it_backend.service.EventService;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Event>> getAllEvent() {
        List<Event> event = eventService.findAll();
        return ResponseEntity.ok(event);
    }

    @GetMapping("/by-tags")
    public ResponseEntity<List<Event>> getAllEventByTags(
            @RequestParam(required = true) Set<NewsTag> tags) {
        List<Event> event = eventService.findByTags(tags);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/status/{userId}")
    public ResponseEntity<EventStatus> getStatusById(@PathVariable long userId, @RequestParam long eventId){
        Event event = eventService.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));;
        return ResponseEntity.ok(eventService.findStatusById(userId, event));
    }

    @PutMapping("register/{userId}")
    public ResponseEntity<?> addUserInList(@PathVariable long userId, @RequestParam long eventId){
        Event event = eventService.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));;
        eventService.addUser(userId, event);
        return ResponseEntity.ok(userId);
    }
}
