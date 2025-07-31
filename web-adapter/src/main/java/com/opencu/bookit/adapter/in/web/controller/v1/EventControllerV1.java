package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.adapter.in.web.dto.request.UpdateEventRequest;
import com.opencu.bookit.adapter.in.web.dto.response.EventResponseV1;
import com.opencu.bookit.adapter.in.web.exception.ResourceNotFoundException;
import com.opencu.bookit.adapter.in.web.mapper.EventResponseMapperV1;
import com.opencu.bookit.application.service.event.EventService;
import com.opencu.bookit.application.service.photo.PhotoService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/v1/events")
public class EventControllerV1 {
    private final EventService eventService;
    private final PhotoService photoService;
    private final EventResponseMapperV1 eventResponseMapper;

    public EventControllerV1(EventService eventService, PhotoService photoService, EventResponseMapperV1 eventResponseMapper) {
        this.eventService = eventService;
        this.photoService = photoService;
        this.eventResponseMapper = eventResponseMapper;
    }

    @Operation(
            summary = "Get all public events with optional filters",
            description = "startDate and endDate are days event.startTime is between"
    )
    @GetMapping
    public ResponseEntity<Page<EventResponseV1>> getAllEvents(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Set<ThemeTags> tags,
            @RequestParam(required = false) Set<ContentFormat> formats,
            @RequestParam(required = false) Set<ContentTime> times,
            @RequestParam(required = false) Set<ParticipationFormat> participationFormats,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "${pagination.default-page}") int page,
            @RequestParam(defaultValue = "${pagination.default-size}") int size,
            @RequestParam(defaultValue = "startTime,asc") String sort
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
            Page<EventResponseV1> eventsPage = eventService
                    .findWithFilters(startDate, endDate, tags, formats, times, participationFormats, search, status, pageable, currentUserId)
                    .map(event -> {
                        try {
                            return eventResponseMapper.toEventResponse(event);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
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
                                       .orElseThrow(() -> new ResourceNotFoundException("Event " + eventId + " not found"));

        return ResponseEntity.ok(eventService.findStatusById(currentUser.getId(), event));
    }

    @Operation(summary = "Register current user for the event")
    @PutMapping("/registrations/{eventId}")
    public ResponseEntity<EventResponseV1> registerForEvent(@PathVariable UUID eventId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetailsImpl currentUser = (UserDetailsImpl) authentication.getPrincipal();
        if (currentUser.getStatus() != UserStatus.VERIFIED) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        EventModel event = eventService.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event " + eventId + " not found"));
        eventService.addUser(currentUser.getId(), event);
        EventResponseV1 eventResponse = null;
        try {
            eventResponse = eventResponseMapper.toEventResponse(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(eventResponse);
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
                .orElseThrow(() -> new ResourceNotFoundException("Event " + eventId + " not found"));
        eventService.removeUser(currentUser.getId(), event);
        return ResponseEntity.ok("User removed successfully");
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(@securityService.getAdmin())")
    @Operation(summary = "Get event by id")
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseV1> getById(
        @PathVariable UUID eventId
    ) {
        Optional<EventModel> eventOpt = eventService.findById(eventId);
        if (eventOpt.isEmpty()) {
            throw new NoSuchElementException("Event " + eventId + " not found");
        }
        try {
            return ResponseEntity.ok(eventResponseMapper.toEventResponse(eventOpt.get()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(@securityService.getAdmin())")
    @Operation(
            summary = "Update event by id. FOR ADMINS ONLY!",
            description = "Content-type: multipart/form-data, see Postman tests for more details."
    )
    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponseV1> updateEvent(
            @PathVariable UUID eventId,
            @RequestPart("updateEventRequest") UpdateEventRequest updateEventRequest,
            @RequestPart("photos") List<MultipartFile> photos
    ) {
        try {
            List<String> keys = null;
            keys = photoService.upload(photos);
            EventModel eventModel = eventService.updateEvent(
                    eventId,
                    updateEventRequest.name(),
                    updateEventRequest.description(),
                    updateEventRequest.tags(),
                    updateEventRequest.formats(),
                    updateEventRequest.times(),
                    updateEventRequest.participationFormats(),
                    keys,
                    updateEventRequest.startTime(),
                    updateEventRequest.endTime(),
                    updateEventRequest.available_places(),
                    updateEventRequest.areaId()
            );
                return ResponseEntity.ok(eventResponseMapper.toEventResponse(eventModel));
            } catch (IOException e) {
            return ResponseEntity.badRequest().build();
            } catch (NoSuchElementException e) {
                return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(@securityService.getAdmin())")
    @Operation(
            summary = "Update event. FOR ADMINS ONLY!",
            description = "Content-type: multipart/form-data, see Postman tests for more details."
    )
    @PostMapping
    public ResponseEntity<EventResponseV1> createEvent(
            @RequestPart("updateEventRequest") UpdateEventRequest updateEventRequest,
            @RequestPart("photos") List<MultipartFile> photos
    ) {
        List<String> keys = null;
        try {
            keys = photoService.upload(photos);
            EventModel eventModel = eventService.createEvent(
                updateEventRequest.name(),
                updateEventRequest.description(),
                updateEventRequest.tags(),
                updateEventRequest.formats(),
                updateEventRequest.times(),
                updateEventRequest.participationFormats(),
                keys,
                updateEventRequest.startTime(),
                updateEventRequest.endTime(),
                updateEventRequest.available_places(),
                updateEventRequest.areaId()
        );
            return ResponseEntity.status(HttpStatus.CREATED).body(eventResponseMapper.toEventResponse(eventModel));
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(@securityService.getAdmin())")
    @Operation(summary = "Delete event from database")
    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(
            @PathVariable UUID eventId
    ) {
        eventService.deleteById(eventId);
        return ResponseEntity.ok("Event removed successfully");
    }
}