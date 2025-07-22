package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.adapter.in.web.dto.request.AdminUpdateBookingRequest;
import com.opencu.bookit.adapter.in.web.dto.request.CreateBookingRequest;
import com.opencu.bookit.adapter.in.web.dto.request.UpdateBookingRequest;
import com.opencu.bookit.adapter.in.web.dto.response.BookingResponse;
import com.opencu.bookit.adapter.in.web.exception.ApiError;
import com.opencu.bookit.adapter.in.web.exception.ProfileNotCompletedException;
import com.opencu.bookit.adapter.in.web.mapper.BookingRequestMapper;
import com.opencu.bookit.adapter.in.web.mapper.BookingResponseMapper;
import com.opencu.bookit.adapter.out.security.spring.service.SecurityService;
import com.opencu.bookit.application.service.booking.BookingService;
import com.opencu.bookit.adapter.out.security.spring.service.UserDetailsImpl;
import com.opencu.bookit.domain.model.booking.BookingModel;
import com.opencu.bookit.domain.model.user.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final BookingResponseMapper bookingResponseMapper;
    private final BookingRequestMapper bookingRequestMapper;
    private final SecurityService securityService;

    public BookingControllerV1(BookingService bookingService,
                               BookingResponseMapper bookingResponseMapper, BookingRequestMapper bookingRequestMapper, SecurityService securityService) {
        this.bookingService = bookingService;
        this.bookingResponseMapper = bookingResponseMapper;
        this.bookingRequestMapper = bookingRequestMapper;
        this.securityService = securityService;
    }

    @Operation(summary = "Get available dates for area")
    @GetMapping("/availability/dates")
    public List<LocalDate> findAvailableDates(@RequestParam Optional<UUID> areaId) {
        return bookingService.findAvailableDates(areaId);
    }

    @Operation(summary = "Get available times for a date and area")
    @GetMapping("/availability/times")
    public ResponseEntity<List<List<String>>> findAvailableTimeByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Optional<UUID> areaId,
            @RequestParam(required = false) Optional<UUID> bookingId) {

        List<List<Pair<LocalDateTime, LocalDateTime>>> times = bookingService.findAvailableTime(date, areaId, bookingId);
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
        return bookingService.findClosestAvailableTimes(areaId).stream()
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
        List<UUID> availableAreas = bookingService.findAvailableAreas(startTimes);
        return ResponseEntity.ok(availableAreas);
    }

    @Operation(summary = "Get current user's bookings by timeline")
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getBookings(
            @RequestParam(defaultValue = "current") String timeline,
            @RequestParam(defaultValue = "${pagination.default-page}") int page,
            @RequestParam(defaultValue = "${pagination.default-size}") int size
                                                            ) {
        UserDetailsImpl currentUser = getCurrentUser();
        List<BookingModel> bookings;
        switch (timeline) {
            case "future" -> bookings = new ArrayList<>(bookingService.getFutureBookings(currentUser.getId()));
            case "past" -> bookings = new ArrayList<>(bookingService.getPastBookings(currentUser.getId()));
            default -> bookings = new ArrayList<>(bookingService.getCurrentBookings(currentUser.getId()));
        }
        bookings.sort(Comparator.comparing(BookingModel::getStartTime));
        int fromIndex = Math.min(page * size, bookings.size());
        int toIndex = Math.min(fromIndex + size, bookings.size());
        return ResponseEntity.ok(bookingResponseMapper.toResponseList(bookings.subList(fromIndex, toIndex)));
    }

    @Operation(summary = "Get current area's bookings by user and date")
    @GetMapping("/advanced")
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
                .findWithFilters(pageable, areaId, userId)
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

        List<BookingModel> createdBooking = bookingService.createBooking(bookingRequestMapper.toCommand(request));
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
            UserDetailsImpl currentUser = getCurrentUser();
            if (currentUser.getStatus() != UserStatus.VERIFIED) {
                throw new ProfileNotCompletedException("User profile is not completed. Please complete your profile before updating bookings.");
            }

            BookingModel updatedBooking = bookingService.updateBooking(bookingId, bookingRequestMapper.toQuery(request));
            return ResponseEntity.ok(bookingResponseMapper.toResponse(updatedBooking));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (ProfileNotCompletedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(SecurityService.getAdmin())")
    @PutMapping("/admin/{bookingId}")
    public ResponseEntity<BookingResponse> updateById(
            @PathVariable UUID bookingId,
            @RequestBody AdminUpdateBookingRequest adminUpdateBookingRequest
    ) {
        try {
            BookingResponse response = bookingResponseMapper.toResponse(
                    bookingService.updateById(
                            bookingId,
                            adminUpdateBookingRequest.userId(),
                            adminUpdateBookingRequest.areaId(),
                            adminUpdateBookingRequest.startTime(),
                            adminUpdateBookingRequest.endTime(),
                            adminUpdateBookingRequest.status()
                    )
            );
            return ResponseEntity.ok(response);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{bookingId}")
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
