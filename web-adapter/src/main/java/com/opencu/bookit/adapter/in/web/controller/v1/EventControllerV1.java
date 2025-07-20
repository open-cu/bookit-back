package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.adapter.in.web.dto.request.UpdateEventRequest;
import com.opencu.bookit.adapter.in.web.dto.response.EventResponse;
import com.opencu.bookit.adapter.in.web.exception.ResourceNotFoundException;
import com.opencu.bookit.adapter.in.web.mapper.EventResponseMapper;
import com.opencu.bookit.application.service.event.EventService;
import com.opencu.bookit.domain.model.contentcategory.ContentFormat;
import com.opencu.bookit.domain.model.contentcategory.ContentTime;
import com.opencu.bookit.domain.model.contentcategory.ParticipationFormat;
import com.opencu.bookit.domain.model.event.EventModel;
import com.opencu.bookit.domain.model.event.EventStatus;
import com.opencu.bookit.domain.model.contentcategory.ThemeTags;
import com.opencu.bookit.domain.model.user.UserStatus;
import com.opencu.bookit.adapter.out.security.spring.service.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;
import java.util.Optional;
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
            @RequestParam(required = false) Set<ContentFormat> formats,
            @RequestParam(required = false) Set<ContentTime> times,
            @RequestParam(required = false) Set<ParticipationFormat> participationFormats,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date,asc") String sort
                                                           ) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

        UUID currentUserId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl user) {
            currentUserId = user.getId();
        }

        if ((("registered".equalsIgnoreCase(status) || "available".equalsIgnoreCase(status)) && currentUserId == null)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Page<EventResponse> eventsPage = eventService
                .findWithFilters(tags, formats, times, participationFormats, search, status, pageable, currentUserId)
                .map(eventResponseMapper::toEventResponse);
        return ResponseEntity.ok(eventsPage);
    }

    @Operation(summary = "Get registration status for current user and event")
    @GetMapping("/registrations/{eventId}/status")
    public ResponseEntity<EventStatus> getRegistrationStatus(@PathVariable UUID eventId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetailsImpl currentUser = (UserDetailsImpl) authentication.getPrincipal();

        EventModel event = eventService.findById(eventId)
                                       .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        return ResponseEntity.ok(eventService.findStatusById(currentUser.getId(), event));
    }

    @Operation(summary = "Register current user for the event")
    @PutMapping("/registrations/{eventId}")
    public ResponseEntity<EventModel> registerForEvent(@PathVariable UUID eventId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetailsImpl currentUser = (UserDetailsImpl) authentication.getPrincipal();
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
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetailsImpl currentUser = (UserDetailsImpl) authentication.getPrincipal();
        if (currentUser.getStatus() != UserStatus.VERIFIED) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only verified users can perform this action");
        }

        EventModel event = eventService.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        eventService.removeUser(currentUser.getId(), event);
        return ResponseEntity.ok("User removed successfully");
    }

    @PreAuthorize("@securityService.hasRoleAdminOrIsDev()")
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getById(
        @PathVariable UUID eventId
    ) {
        Optional<EventModel> eventOpt = eventService.findById(eventId);
        if (eventOpt.isEmpty()) {
            throw new NoSuchElementException("No such event found");
        }
        return ResponseEntity.ok(eventResponseMapper.toEventResponse(eventOpt.get()));
    }

    @PreAuthorize("@securityService.hasRoleAdminOrIsDev()")
    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable UUID eventId,
            @RequestBody UpdateEventRequest updateEventRequest
    ) {
        try {
            EventModel eventModel = eventService.updateEvent(
                    eventId,
                    updateEventRequest.name(),
                    updateEventRequest.description(),
                    updateEventRequest.tags(),
                    updateEventRequest.formats(),
                    updateEventRequest.times(),
                    updateEventRequest.participationFormats(),
                    updateEventRequest.date(),
                    updateEventRequest.available_places()
            );
            return ResponseEntity.ok(eventResponseMapper.toEventResponse(eventModel));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }

    }

    @PreAuthorize("@securityService.hasRoleAdminOrIsDev()")
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @RequestBody UpdateEventRequest updateEventRequest
    ) {
        EventModel eventModel = eventService.createEvent(
                updateEventRequest.name(),
                updateEventRequest.description(),
                updateEventRequest.tags(),
                updateEventRequest.formats(),
                updateEventRequest.times(),
                updateEventRequest.participationFormats(),
                updateEventRequest.date(),
                updateEventRequest.available_places()
        );
        return ResponseEntity.ok(eventResponseMapper.toEventResponse(eventModel));
    }

    @PreAuthorize("@securityService.hasRoleAdminOrIsDev()")
    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(
            @PathVariable UUID eventId
    ) {
        eventService.deleteById(eventId);
        return ResponseEntity.ok("Event removed successfully");
    }
}