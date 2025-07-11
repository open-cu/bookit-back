package ru.tbank.bookit.book_it_backend.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
public class EventControllerV1 {
    private final EventService eventService;

    public EventControllerV1(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(summary = "Get all events with optional filters")
    @GetMapping
    public ResponseEntity<Page<EventResponse>> getAllEvents(
            @RequestParam(required = false) Set<ThemeTags> tags,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date,asc") String sort
    ) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortBy = sortParams[0];
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        UUID currentUserId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            currentUserId = userDetails.getId();
        }

        if ((("registered".equalsIgnoreCase(status) || "available".equalsIgnoreCase(status)) && currentUserId == null)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Page<EventResponse> events = eventService.findWithFilters(tags, search, status, pageable, currentUserId);
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Get registration status for current user and event")
    @GetMapping("/registrations/{eventId}/status")
    public ResponseEntity<EventStatus> getRegistrationStatus(@PathVariable UUID eventId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Event event = eventService.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        return ResponseEntity.ok(eventService.findStatusById(userDetails.getId(), event));
    }

    @Operation(summary = "Register current user for the event")
    @PutMapping("/registrations/{eventId}")
    public ResponseEntity<Event> registerForEvent(@PathVariable UUID eventId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (userDetails.getStatus() != UserStatus.VERIFIED) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Event event = eventService.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        eventService.addUser(userDetails.getId(), event);
        return ResponseEntity.ok(event);
    }

    @Operation(summary = "Remove current user from event registrations")
    @DeleteMapping("/registrations/{eventId}")
    public ResponseEntity<String> unregisterFromEvent(@PathVariable UUID eventId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (userDetails.getStatus() != UserStatus.VERIFIED) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only verified users can perform this action");
        }

        Event event = eventService.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        eventService.removeUser(userDetails.getId(), event);
        return ResponseEntity.ok("User removed successfully");
    }
}