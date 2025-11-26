package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.adapter.in.web.dto.request.CreateEventApplicationRequest;
import com.opencu.bookit.adapter.in.web.dto.response.EventApplicationResponse;
import com.opencu.bookit.adapter.in.web.mapper.EventApplicationResponseMapper;
import com.opencu.bookit.application.port.in.CreateEventApplicationUseCase;
import com.opencu.bookit.application.service.eventapplication.EventApplicationService;
import com.opencu.bookit.domain.model.user.UserStatus;
import com.opencu.bookit.domain.model.event.EventApplicationStatus;
import com.opencu.bookit.adapter.out.security.spring.service.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
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

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/event-applications")
@RequiredArgsConstructor
public class EventApplicationController {
    private final EventApplicationService eventApplicationService;
    private final CreateEventApplicationUseCase createEventApplicationUseCase;
    private final EventApplicationResponseMapper responseMapper;

    @Operation(summary = "Get all event applications with optional filters (FOR ADMINS ONLY!)")
    @PreAuthorize("not @securityService.isProd() or @securityService.hasRequiredRole(@securityService.getAdmin())")
    @GetMapping
    public ResponseEntity<Page<EventApplicationResponse>> getAllEventApplications(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) UUID eventId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDateTo,
            @RequestParam(required = false) String cityOfResidence,
            @RequestParam(required = false) String activityDetails,
            @RequestParam(required = false) EventApplicationStatus status,
            @RequestParam(defaultValue = "${pagination.default-page}") int page,
            @RequestParam(defaultValue = "${pagination.default-size}") int size,
            @RequestParam(defaultValue = "createdAt,asc") String sort
    ) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

        Page<EventApplicationResponse> responsePage = eventApplicationService.findWithFilters(
                userId, eventId, birthDateFrom, birthDateTo, cityOfResidence, activityDetails, status, pageable).map(responseMapper::toResponse);

        return ResponseEntity.ok(responsePage);
    }

    @Operation(summary = "Create a new application for an event")
    @PostMapping("/{eventId}")
    public ResponseEntity<EventApplicationResponse> createApplication(
            @PathVariable UUID eventId,
            @Valid @RequestBody CreateEventApplicationRequest request
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl currentUser = (UserDetailsImpl) authentication.getPrincipal();

        if (currentUser.getStatus() != UserStatus.VERIFIED) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var newApplication = createEventApplicationUseCase.createEventApplication(
                eventId,
                currentUser.getId(),
                request.cityOfResidence(),
                request.dateOfBirth(),
                request.activityDetails()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseMapper.toResponse(newApplication));
    }

    @Operation(summary = "Delete own event application")
    @DeleteMapping("/{applicationId}")
    public ResponseEntity<String> deleteOwnApplication(
            @PathVariable UUID applicationId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl currentUser = (UserDetailsImpl) authentication.getPrincipal();

        if (currentUser.getStatus() != UserStatus.VERIFIED) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        eventApplicationService.deleteByUser(applicationId, currentUser.getId());
        return ResponseEntity.ok("Event application deleted successfully");
    }

    @Operation(summary = "Get current user's event applications with optional status filter")
    @GetMapping("/my")
    public ResponseEntity<Page<EventApplicationResponse>> getMyEventApplications(
            @RequestParam(required = false) EventApplicationStatus status,
            @RequestParam(defaultValue = "${pagination.default-page}") int page,
            @RequestParam(defaultValue = "${pagination.default-size}") int size,
            @RequestParam(defaultValue = "createdAt,asc") String sort
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl currentUser = (UserDetailsImpl) authentication.getPrincipal();

        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

        Page<EventApplicationResponse> responsePage = eventApplicationService.findWithFilters(
                currentUser.getId(), null, null, null, null, null, status, pageable
        ).map(responseMapper::toResponse);

        return ResponseEntity.ok(responsePage);
    }

    @Operation(summary = "Update status of an event application (FOR ADMINS ONLY!)")
    @PreAuthorize("not @securityService.isProd() or @securityService.hasRequiredRole(@securityService.getAdmin())")
    @PatchMapping("/{applicationId}/status")
    public ResponseEntity<EventApplicationResponse> updateApplicationStatus(
            @PathVariable UUID applicationId,
            @RequestParam EventApplicationStatus status) {
        eventApplicationService.updateStatus(applicationId, status);
        return ResponseEntity.ok().build();
    }
}
