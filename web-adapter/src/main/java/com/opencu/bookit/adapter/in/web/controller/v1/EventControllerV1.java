package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.adapter.in.web.dto.response.EventResponse;
import com.opencu.bookit.adapter.in.web.exception.ResourceNotFoundException;
import com.opencu.bookit.adapter.in.web.mapper.EventResponseMapper;
import com.opencu.bookit.application.service.event.EventService;
import com.opencu.bookit.domain.model.event.EventModel;
import com.opencu.bookit.domain.model.event.EventStatus;
import com.opencu.bookit.domain.model.event.ThemeTags;
import com.opencu.bookit.domain.model.user.UserModel;
import com.opencu.bookit.domain.model.user.UserStatus;
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

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
public class EventControllerV1 {
    private final EventService eventService;
    private final EventResponseMapper eventResponseMapper;

    public EventControllerV1(EventService eventService, EventResponseMapper eventResponseMapper) {
        this.eventService = eventService;
        this.eventResponseMapper = eventResponseMapper;
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
        if (authentication != null && authentication.getPrincipal() instanceof UserModel user) {
            currentUserId = user.getId();
        }

        if ((("registered".equalsIgnoreCase(status) || "available".equalsIgnoreCase(status)) && currentUserId == null)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Page<EventResponse> events = eventService.findWithFilters(tags, search, status, pageable, currentUserId)
                                                 .map(eventResponseMapper::toEventResponse);
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Get registration status for current user and event")
    @GetMapping("/registrations/{eventId}/status")
    public ResponseEntity<EventStatus> getRegistrationStatus(@PathVariable UUID eventId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserModel)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserModel currentUser = (UserModel) authentication.getPrincipal();

        EventModel event = eventService.findById(eventId)
                                       .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        return ResponseEntity.ok(eventService.findStatusById(currentUser.getId(), event));
    }

    @Operation(summary = "Register current user for the event")
    @PutMapping("/registrations/{eventId}")
    public ResponseEntity<EventModel> registerForEvent(@PathVariable UUID eventId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserModel)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserModel currentUser = (UserModel) authentication.getPrincipal();
        if (currentUser.getStatus() != UserStatus.VERIFIED) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        EventModel event = eventService.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        eventService.addUser(currentUser.getId(), event);
        return ResponseEntity.ok(event);
    }

    @Operation(summary = "Remove current user from event registrations")
    @DeleteMapping("/registrations/{eventId}")
    public ResponseEntity<String> unregisterFromEvent(@PathVariable UUID eventId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserModel)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserModel currentUser = (UserModel) authentication.getPrincipal();
        if (currentUser.getStatus() != UserStatus.VERIFIED) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only verified users can perform this action");
        }

        EventModel event = eventService.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        eventService.removeUser(currentUser.getId(), event);
        return ResponseEntity.ok("User removed successfully");
    }
}