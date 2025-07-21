package com.opencu.bookit.adapter.in.web.controller;

import com.opencu.bookit.adapter.in.web.dto.response.EventResponse;
import com.opencu.bookit.adapter.in.web.exception.ResourceNotFoundException;
import com.opencu.bookit.adapter.in.web.mapper.EventResponseMapper;
import com.opencu.bookit.adapter.out.security.spring.service.UserDetailsImpl;
import com.opencu.bookit.application.service.event.EventService;
import com.opencu.bookit.domain.model.event.EventModel;
import com.opencu.bookit.domain.model.event.EventStatus;
import com.opencu.bookit.domain.model.contentcategory.ThemeTags;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;
    private final EventResponseMapper eventResponseMapper;

    public EventController(EventService eventService, EventResponseMapper eventResponseMapper) {
        this.eventService = eventService;
        this.eventResponseMapper = eventResponseMapper;
    }

    @Operation(description = "Returns information in the list format about all events")
    @GetMapping()
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        List<EventResponse> event = null;
        try {
            event = eventResponseMapper.toEventResponseList(eventService.findAll());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(event);
    }

    @Operation(description = "Returns information in list format about all events for a specific tag")
    @GetMapping("/by-tags")
    public ResponseEntity<List<EventResponse>> getAllEventsByTags(
            @RequestParam Set<ThemeTags> tags) {
        List<EventResponse> event = null;
        try {
            event = eventResponseMapper.toEventResponseList(eventService.findByTags(tags));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(event);
    }

    @Operation(description = "Returns information about the event status by user")
    @GetMapping("{eventId}/status/{userId}")
    public ResponseEntity<EventStatus> getStatusById(@PathVariable UUID eventId, @PathVariable UUID userId){
        EventModel event = eventService.findById(eventId).orElseThrow(
                () -> new ResourceNotFoundException("Event not found"));
        return ResponseEntity.ok(eventService.findStatusById(userId, event));
    }

    @Operation(description = "Registers (by entering the guest list of the event) the user for this event")
    @PutMapping("/{eventId}/registrations/{userId}")
    public ResponseEntity<EventResponse> addUserInList(@PathVariable UUID eventId, @PathVariable UUID userId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        EventModel event = eventService.findById(eventId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        EventStatus status = eventService.findStatusById(userDetails.getId(), event);
        if (status != EventStatus.AVAILABLE && !eventService.isUserPresent(userDetails.getId(), event)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        eventService.addUser(userId, event);
        try {
            return ResponseEntity.ok(eventResponseMapper.toEventResponse(event));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(description = "returns information about the created event")
    @PostMapping("/event")
    public ResponseEntity<EventResponse> createEvent(@RequestBody EventModel event) {
        EventModel savedEvent = eventService.saveEvent(event);
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(eventResponseMapper.toEventResponse(savedEvent));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(description = "Deletes the user from the guest list for the event, returns a string about the success of the deletion")
    @DeleteMapping("/{eventId}/registrations/{userId}")
    public ResponseEntity<String> removeUserInList(@PathVariable UUID eventId, @PathVariable UUID userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        EventModel event = eventService.findById(eventId).orElseThrow(
                () -> new ResourceNotFoundException("Event not found"));

        if (!eventService.isUserPresent(userDetails.getId(), event)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only registered users can perform this action");
        }

        eventService.removeUser(userId, event);
        return ResponseEntity.ok("User removed successfully");
    }
}