package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.adapter.in.web.dto.request.CreateBookingRequest;
import com.opencu.bookit.adapter.in.web.dto.request.UpdateBookingRequest;
import com.opencu.bookit.adapter.in.web.dto.response.BookingResponse;
import com.opencu.bookit.adapter.in.web.exception.ApiError;
import com.opencu.bookit.adapter.in.web.exception.ProfileNotCompletedException;
import com.opencu.bookit.adapter.in.web.mapper.BookingRequestMapper;
import com.opencu.bookit.adapter.in.web.mapper.BookingResponseMapper;
import com.opencu.bookit.adapter.out.security.spring.service.SecurityService;
import com.opencu.bookit.application.service.booking.AvailabilityService;
import com.opencu.bookit.application.service.booking.BookingService;
import com.opencu.bookit.adapter.out.security.spring.service.UserDetailsImpl;
import com.opencu.bookit.domain.model.booking.BookingModel;
import com.opencu.bookit.domain.model.booking.ValidationRule;
import com.opencu.bookit.domain.model.user.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.*;
import org.springframework.data.util.Pair;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingControllerV1 {
    private final BookingService bookingService;
    private final AvailabilityService availabilityService;
    private final BookingResponseMapper bookingResponseMapper;
    private final BookingRequestMapper bookingRequestMapper;
    private final SecurityService securityService;

    public BookingControllerV1(BookingService bookingService, AvailabilityService availabilityService,
                               BookingResponseMapper bookingResponseMapper, BookingRequestMapper bookingRequestMapper, SecurityService securityService) {
        this.bookingService = bookingService;
        this.availabilityService = availabilityService;
        this.bookingResponseMapper = bookingResponseMapper;
        this.bookingRequestMapper = bookingRequestMapper;
        this.securityService = securityService;
    }

    @Operation(summary = "Get available dates for area")
    @GetMapping("/availability/dates")
    public List<LocalDate> findAvailableDates(@RequestParam Optional<UUID> areaId) {
        return availabilityService.findAvailableDates(areaId);
    }

    @Operation(summary = "Get available times for a startTime and area")
    @GetMapping("/availability/times")
    public ResponseEntity<List<List<String>>> findAvailableTimeByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Optional<UUID> areaId,
            @RequestParam(required = false) Optional<UUID> bookingId) {

        List<List<Pair<LocalDateTime, LocalDateTime>>> times = availabilityService.findAvailableTime(date, areaId, bookingId);
        List<List<String>> result = new ArrayList<>();
        for (List<Pair<LocalDateTime, LocalDateTime>> l : times) {
            List<String> formattedTimes = l.stream()
                    .map(timePair -> timePair.getFirst() + ";" + timePair.getSecond())
                    .toList();
            result.addLast(formattedTimes);
        }
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get closest available times for area")
    @GetMapping("/availability/closest-times")
    public Set<String> findClosestAvailableTimes(@RequestParam UUID areaId) {
        return availabilityService.findClosestAvailableTimes(areaId).stream()
                             .map(timePair -> timePair.getFirst() + ";" + timePair.getSecond())
                             .collect(Collectors.toCollection(TreeSet::new));
    }

    @Operation(
            summary = "Get available areas for given times",
            responses = {
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @GetMapping("/availability/areas")
    public ResponseEntity<List<UUID>> findAvailableAreas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Set<LocalDateTime> startTimes) {
        if (startTimes == null) {
            startTimes = new HashSet<>();
        }
        List<UUID> availableAreas = availabilityService.findAvailableAreas(startTimes);
        return ResponseEntity.ok(availableAreas);
    }

    @Operation(summary = "Get current user's bookings by timeline. timeline if for user-mode only, rest is for admin only")
    @GetMapping
    public ResponseEntity<Page<BookingResponse>> getBookings(
            @RequestParam(required = false) String timeline,
            @RequestParam(required = false) UUID areaId,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "${pagination.default-page}") int page,
            @RequestParam(defaultValue = "${pagination.default-size}") int size
                                                            ) {

        if (areaId != null && userId != null && timeline != null) {
            return ResponseEntity.badRequest().build();
        }

        List<BookingModel> bookings = new ArrayList<>();
        Sort.Direction direction = Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(direction, "startTime"));

        if (timeline != null && !timeline.isEmpty()) {
            UserDetailsImpl currentUser = getCurrentUser();
            switch (timeline.toLowerCase()) {
                case "future" -> bookings.addAll(bookingService.getFutureBookings(currentUser.getId()));
                case "past" -> bookings.addAll(bookingService.getPastBookings(currentUser.getId()));
                case "current" -> bookings.addAll(bookingService.getCurrentBookings(currentUser.getId()));
                default -> {
                    throw new IllegalArgumentException("timeline " + timeline + " is not recognized, use future, past or current");
                }
            }
            int fromIndex = Math.min(page * size, bookings.size());
            int toIndex = Math.min(fromIndex + size, bookings.size());
            List<BookingResponse> responses = bookingResponseMapper.toResponseList(bookings.subList(fromIndex, toIndex));
            return ResponseEntity.ok(new PageImpl<>(responses, pageable, bookings.size()));
        }

        if (securityService.hasRequiredRole(securityService.getAdmin()) || securityService.isDev()) {
            Page<BookingResponse> bookingResponsePage = bookingService
                    .findWithFilters(startDate, endDate, pageable, areaId, userId)
                    .map(bookingResponseMapper::toResponse);
            return ResponseEntity.ok(bookingResponsePage);
        }
        return ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Deprecated. Use Get /api/v1/bookings instead")
    @GetMapping("/advanced")
    @Deprecated
    public ResponseEntity<Page<BookingResponse>> getBookingsAdvanced(
            @RequestParam (required = false) UUID areaId,
            @RequestParam (required = false) UUID userId,
            @RequestParam  (required = false) String date,
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "10") int size
            ) {
        Sort.Direction direction = Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(direction, "startTime"));

        Page<BookingResponse> bookingResponsePage = bookingService
                .findWithFilters(null, null, pageable, areaId, userId)
                .map(bookingResponseMapper::toResponse);
        return ResponseEntity.ok(bookingResponsePage);
    }

    @Operation(summary = "Create a new booking")
    @PostMapping
    public Set<ResponseEntity<BookingResponse>> createBooking(@RequestBody CreateBookingRequest request) {
        UserDetailsImpl currentUser = getCurrentUser();
        if (currentUser.getStatus() != UserStatus.VERIFIED) {
            throw new ProfileNotCompletedException("User profile is not completed. Please complete your profile before creating bookings.");
        }

        for (Pair<LocalDateTime, LocalDateTime> t : request.timePeriods()) {
            if (t.getFirst().isAfter(t.getSecond())) {
                HashSet<ResponseEntity<BookingResponse>> res = new HashSet<>();
                res.add(ResponseEntity.badRequest().build());
                return res;
            }
        }

        Set<ValidationRule> validationRules = Set.of(ValidationRule.VALIDATE_AREA_AVAILABILITY, ValidationRule.VALIDATE_TIME_RESTRICTIONS,
                ValidationRule.VALIDATE_USER_OWNERSHIP, ValidationRule.VALIDATE_USER_BOOKING_CONFLICTS);
        List<BookingModel> createdBooking = bookingService.createBooking(bookingRequestMapper.toCommand(request), validationRules);
        Set<ResponseEntity<BookingResponse>> result = new HashSet<>();

        for (BookingModel b : createdBooking) {
            URI uri = URI.create("/api/v1/bookings/" + b.getId());
            result.add(ResponseEntity.created(uri).body(bookingResponseMapper.toResponse(b)));
        }
        return result;
    }

    @Operation(summary = "Get booking by id")
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable UUID bookingId) {
        Optional<BookingResponse> booking = bookingService.findBooking(bookingId).map(bookingResponseMapper::toResponse);
        return booking.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update booking by id and booking information")
    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable UUID bookingId,
            @RequestBody UpdateBookingRequest request) {
        try {
            if (securityService.hasRequiredRole(securityService.getAdmin()) || securityService.isDev()) {
                if (request.userId() != null || request.status() != null) {
                    BookingResponse response = bookingResponseMapper.toResponse(
                            bookingService.updateById(
                                    bookingId,
                                    request.userId(),
                                    request.areaId(),
                                    request.startTime(),
                                    request.endTime(),
                                    request.status()
                            )
                    );
                    return ResponseEntity.ok(response);
                }
            }

            UserDetailsImpl currentUser = getCurrentUser();
            if (currentUser.getStatus() != UserStatus.VERIFIED) {
                throw new ProfileNotCompletedException("User profile is not completed. Please complete your profile before updating bookings.");
            }
            if (request.userId() != null && request.userId() != currentUser.getId()) {
                throw new IllegalStateException("You cannot update booking for another user.");
            }

            Set<ValidationRule> validateAreaAvailability = Set.of(ValidationRule.VALIDATE_AREA_AVAILABILITY, ValidationRule.VALIDATE_TIME_RESTRICTIONS, ValidationRule.VALIDATE_USER_OWNERSHIP, ValidationRule.VALIDATE_USER_BOOKING_CONFLICTS);
            BookingModel updatedBooking = bookingService.updateBooking(bookingId, bookingRequestMapper.toQuery(request), validateAreaAvailability);
            return ResponseEntity.ok(bookingResponseMapper.toResponse(updatedBooking));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (ProfileNotCompletedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @DeleteMapping("/{bookingId}")
    @Operation(summary = "Admins delete booking, users cancel it if have made a booking before")
    public ResponseEntity<?> deleteById(
            @PathVariable UUID bookingId
    ) {
        if (securityService.hasRequiredRole(securityService.getAdmin())) {
            bookingService.deleteById(bookingId);
            return ResponseEntity.ok("Booking deleted successfully");
        } else {
            bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok("Booking cancelled successfully");
        }
    }

    private UserDetailsImpl getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) authentication.getPrincipal();
    }
}
