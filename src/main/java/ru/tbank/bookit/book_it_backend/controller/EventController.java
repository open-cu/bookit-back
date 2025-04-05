package ru.tbank.bookit.book_it_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.bookit.book_it_backend.model.Event;
import ru.tbank.bookit.book_it_backend.model.EventStatus;
import ru.tbank.bookit.book_it_backend.model.NewsTag;
import ru.tbank.bookit.book_it_backend.service.EventService;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/event")
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
            @PathVariable
            @RequestParam(required = true) Set<NewsTag> tags) {
        List<Event> event = eventService.findByTags(tags);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/status/{userId}")
    public ResponseEntity<EventStatus> getStatusById(@PathVariable long userId, @RequestBody Event event){
        return ResponseEntity.ok(eventService.findStatusById(userId, event));
    }

    @PutMapping("register/{userId}")
    public ResponseEntity<?> addUserInList(@PathVariable long userId, @RequestBody Event event){
        eventService.addUser(userId, event);
        return ResponseEntity.ok(userId);
    }
}
