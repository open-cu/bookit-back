package ru.tbank.bookit.book_it_backend.controller.v1;

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
import ru.tbank.bookit.book_it_backend.repository.EventRepository;
import ru.tbank.bookit.book_it_backend.service.EventService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
public class EventControllerV1 {
    private final EventService eventService;
    private final EventRepository eventRepository; // TODO

    public EventControllerV1(EventService eventService, EventRepository eventRepository) {
        this.eventService = eventService;
        this.eventRepository = eventRepository;
    }

    @Operation(summary = "Get all events with optional filters")
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents(
            @RequestParam(required = false) Set<ThemeTags> tags,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate,asc") String sort
    ) {
        // TODO: реализовать фильтрацию, поиск, сортировку и пагинацию
        if (tags != null && !tags.isEmpty()) {
            return ResponseEntity.ok(eventService.findByTags(tags));
        } else {
            return ResponseEntity.ok(eventService.findAll());
        }
    }

    @Operation(summary = "Get registration status for current user and event")
    @GetMapping("/registrations/{eventId}/status")
    public ResponseEntity<EventStatus> getRegistrationStatus(@PathVariable UUID eventId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User currentUser = (User) authentication.getPrincipal();

        Event event = eventService.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        return ResponseEntity.ok(eventService.findStatusById(currentUser.getId(), event));
    }

    @Operation(summary = "Register current user for the event")
    @PutMapping("/registrations/{eventId}")
    public ResponseEntity<Event> registerForEvent(@PathVariable UUID eventId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User currentUser = (User) authentication.getPrincipal();
        if (currentUser.getStatus() != UserStatus.VERIFIED) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Event event = eventService.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        eventService.addUser(currentUser.getId(), event);
        return ResponseEntity.ok(event);
    }

    @Operation(summary = "Remove current user from event registrations")
    @DeleteMapping("/registrations/{eventId}")
    public ResponseEntity<String> unregisterFromEvent(@PathVariable UUID eventId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User currentUser = (User) authentication.getPrincipal();
        if (currentUser.getStatus() != UserStatus.VERIFIED) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only verified users can perform this action");
        }

        Event event = eventService.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        eventService.removeUser(currentUser.getId(), event);
        return ResponseEntity.ok("User removed successfully");
    }
}