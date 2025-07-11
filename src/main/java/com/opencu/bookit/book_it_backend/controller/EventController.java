package ru.tbank.bookit.book_it_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.tbank.bookit.book_it_backend.DTO.EventResponse;
import ru.tbank.bookit.book_it_backend.exception.ResourceNotFoundException;
import ru.tbank.bookit.book_it_backend.model.*;
import ru.tbank.bookit.book_it_backend.security.services.UserDetailsImpl;
import ru.tbank.bookit.book_it_backend.service.EventService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(description = "Returns information in the list format about all events")
    @GetMapping()
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        List<EventResponse> event = eventService.findAll();
        return ResponseEntity.ok(event);
    }

    @Operation(description = "Returns information in list format about all events for a specific tag")
    @GetMapping("/by-tags")
    public ResponseEntity<List<EventResponse>> getAllEventsByTags(
            @RequestParam Set<ThemeTags> tags) {
        List<EventResponse> event = eventService.findByTags(tags);
        return ResponseEntity.ok(event);
    }

    @Operation(description = "Returns information about the event status by user")
    @GetMapping("{eventId}/status/{userId}")
    public ResponseEntity<EventStatus> getStatusById(@PathVariable UUID eventId, @PathVariable UUID userId){
        Event event = eventService.findById(eventId).orElseThrow(
                () -> new ResourceNotFoundException("Event not found"));
        return ResponseEntity.ok(eventService.findStatusById(userId, event));
    }

    @Operation(description = "Registers (by entering the guest list of the event) the user for this event")
    @PutMapping("/{eventId}/registrations/{userId}")
    public ResponseEntity<Event> addUserInList(@PathVariable UUID eventId, @PathVariable UUID userId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Event event = eventService.findById(eventId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        EventStatus status = eventService.findStatusById(userDetails.getId(), event);
        if (status != EventStatus.AVAILABLE && !eventService.isUserPresent(userDetails.getId(), event)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        eventService.addUser(userId, event);
        return ResponseEntity.ok(event);
    }

    @Operation(description = "returns information about the created event")
    @PostMapping("/event")
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        Event savedEvent = eventService.saveEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
    }

    @Operation(description = "Deletes the user from the guest list for the event, returns a string about the success of the deletion")
    @DeleteMapping("/{eventId}/registrations/{userId}")
    public ResponseEntity<String> removeUserInList(@PathVariable UUID eventId, @PathVariable UUID userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Event event = eventService.findById(eventId).orElseThrow(
                () -> new ResourceNotFoundException("Event not found"));

        if (!eventService.isUserPresent(userDetails.getId(), event)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only registered users can perform this action");
        }

        eventService.removeUser(userId, event);
        return ResponseEntity.ok("User removed successfully");
    }
}