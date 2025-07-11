package ru.tbank.bookit.book_it_backend.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.util.Pair;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.bookit.book_it_backend.DTO.CreateBookingRequest;
import ru.tbank.bookit.book_it_backend.DTO.UpdateBookingRequest;
import ru.tbank.bookit.book_it_backend.exception.ApiError;
import ru.tbank.bookit.book_it_backend.exception.ProfileNotCompletedException;
import ru.tbank.bookit.book_it_backend.model.Booking;
import ru.tbank.bookit.book_it_backend.model.User;
import ru.tbank.bookit.book_it_backend.model.UserStatus;
import ru.tbank.bookit.book_it_backend.service.BookingMenuService;
import ru.tbank.bookit.book_it_backend.service.HomeService;
import ru.tbank.bookit.book_it_backend.service.UserService;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingControllerV1 {
    private final HomeService homeService;
    private final BookingMenuService bookingMenuService;
    private final UserService userService;

    public BookingControllerV1(HomeService homeService, BookingMenuService bookingMenuService, UserService userService) {
        this.homeService = homeService;
        this.bookingMenuService = bookingMenuService;
        this.userService = userService;
    }

    @Operation(summary = "Get available dates for area")
    @GetMapping("/availability/dates")
    public List<LocalDate> findAvailableDates(@RequestParam Optional<UUID> areaId) {
        return bookingMenuService.findAvailableDates(areaId);
    }

    @Operation(summary = "Get available times for a date and area")
    @GetMapping("/availability/times")
    public ResponseEntity<List<List<String>>> findAvailableTimeByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Optional<UUID> areaId,
            @RequestParam(required = false) Optional<UUID> bookingId) {

        List<List<Pair<LocalDateTime, LocalDateTime>>> times = bookingMenuService.findAvailableTime(date, areaId, bookingId);
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
        return bookingMenuService.findClosestAvailableTimes(areaId).stream()
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
        List<UUID> availableAreas = bookingMenuService.findAvailableAreas(startTimes);
        return ResponseEntity.ok(availableAreas);
    }

    @Operation(summary = "Get current user's bookings by timeline")
    @GetMapping
    public ResponseEntity<List<Booking>> getBookings(
            @RequestParam(defaultValue = "current") String timeline,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User currentUser = getCurrentUser();
        List<Booking> bookings;
        switch (timeline) {
            case "future" -> bookings = new ArrayList<>(homeService.getFutureBookings(currentUser.getId()));
            case "past" -> bookings = new ArrayList<>(homeService.getPastBookings(currentUser.getId()));
            default -> bookings = new ArrayList<>(homeService.getCurrentBookings(currentUser.getId()));
        }
        bookings.sort(Comparator.comparing(Booking::getStartTime));
        int fromIndex = Math.min(page * size, bookings.size());
        int toIndex = Math.min(fromIndex + size, bookings.size());
        return ResponseEntity.ok(bookings.subList(fromIndex, toIndex));
    }

    @Operation(summary = "Cancel booking by ID")
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<String> cancelBooking(@PathVariable UUID bookingId) {
        homeService.cancelBooking(bookingId);
        return ResponseEntity.ok("Booking cancelled successfully");
    }

    @Operation(summary = "Create a new booking")
    @PostMapping
    public Set<ResponseEntity<Booking>> createBooking(@RequestBody CreateBookingRequest request) {
        User currentUser = getCurrentUser();
        if (currentUser.getStatus() != UserStatus.VERIFIED) {
            throw new ProfileNotCompletedException("User profile is not completed. Please complete your profile before creating bookings.");
        }

        for (Pair<LocalDateTime, LocalDateTime> t : request.timePeriods()) {
            if (t.getFirst().isAfter(t.getSecond())) {
                HashSet<ResponseEntity<Booking>> res = new HashSet<>();
                res.add(ResponseEntity.badRequest().build());
                return res;
            }
        }

        CreateBookingRequest actualRequest = new CreateBookingRequest(
                currentUser.getId(),
                request.areaId(),
                request.timePeriods(),
                request.quantity()
        );

        Set<Booking> createdBooking = bookingMenuService.createBooking(actualRequest);
        Set<ResponseEntity<Booking>> result = new HashSet<>();

        for (Booking b : createdBooking) {
            URI uri = URI.create("/api/v1/bookings/" + b.getId());
            result.add(ResponseEntity.created(uri).body(b));
        }
        return result;
    }

    @Operation(summary = "Get booking by id")
    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> getBooking(@PathVariable UUID bookingId) {
        Optional<Booking> booking = bookingMenuService.findBooking(bookingId);
        return booking.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update booking by id and booking information")
    @PutMapping("/{bookingId}")
    public ResponseEntity<Booking> updateBooking(
            @PathVariable UUID bookingId,
            @RequestBody UpdateBookingRequest request) {
        try {
            User currentUser = getCurrentUser();
            if (currentUser.getStatus() != UserStatus.VERIFIED) {
                throw new ProfileNotCompletedException("User profile is not completed. Please complete your profile before updating bookings.");
            }

            Booking updatedBooking = bookingMenuService.updateBooking(bookingId, request);
            return ResponseEntity.ok(updatedBooking);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (ProfileNotCompletedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    private User getCurrentUser() {
        return userService.getCurrentUser();
    }
}