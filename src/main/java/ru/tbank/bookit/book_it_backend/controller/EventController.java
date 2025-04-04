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
    public EventStatus getStatusById(@PathVariable long userId, Event event){
        String filePath = "src/main/resources/data.json";
        if (isIdPresent(userId, event.getUser_list())) {
            return EventStatus.PARTICIPATE;
        } else if (event.getAvailable_places() > 0) {
            return EventStatus.REGISTER;
        } else {
            return EventStatus.FULL;
        }
    }

    @PutMapping("register/{userId}")
    public void addUserInList(@PathVariable long userId, Event event){
        if (!isIdPresent(userId, event.getUser_list()) && event.getAvailable_places() > 0) {
            event.setUser_list(event.getUser_list() + "\n" + userId);
            event.setAvailable_places(event.getAvailable_places() - 1);
        }
    }

    public boolean isIdPresent(@PathVariable long userId, String users){
        String[] lines = users.split("\n");
        for (String line : lines) {
                long num = Long.parseLong(line.trim());
                if (num == userId) {
                    return true;
                }
        }
        return false;
    }




}
