package ru.tbank.bookit.book_it_backend.controller;

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
import ru.tbank.bookit.book_it_backend.model.Area;
import ru.tbank.bookit.book_it_backend.model.AreaStatus;
import ru.tbank.bookit.book_it_backend.model.Booking;
import ru.tbank.bookit.book_it_backend.model.User;
import ru.tbank.bookit.book_it_backend.model.UserStatus;
import ru.tbank.bookit.book_it_backend.repository.AreaRepository;
import ru.tbank.bookit.book_it_backend.service.BookingMenuService;
import ru.tbank.bookit.book_it_backend.service.UserService;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/booking-menu")
public class BookingMenuController {
    private final BookingMenuService bookingMenuService;
    private final AreaRepository areaRepository;
    private final UserService userService;

    public BookingMenuController(BookingMenuService bookingMenuService, AreaRepository areaRepository, UserService userService) {
        this.bookingMenuService = bookingMenuService;
        this.areaRepository = areaRepository;
        this.userService = userService;
    }

    @Operation(description = "returns information in the list format about the available dates")
    @GetMapping("/available-date")
    public List<LocalDate> findAvailableDates(@RequestParam Optional<UUID> areaId) {
        return bookingMenuService.findAvailableDates(areaId);
    }

    @Operation(description = "returns list of available time by date separated by ; (start_time;end_time)")
    @GetMapping("/available-time/{date}")
    public ResponseEntity<List<List<String>>> findAvailableTimeByDate(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Optional<UUID> areaId,
            @RequestParam(required = false) Optional<UUID> bookingId) {

        List<List<Pair<LocalDateTime, LocalDateTime>>> times = bookingMenuService.findAvailableTime(date, areaId, bookingId);
        List<List<String>> result = new ArrayList<>();

        for (List<Pair<LocalDateTime, LocalDateTime>> l : times) {
            List<String> formattedTimes =
                    l.stream()
                            .map(timePair -> {
                                return timePair.getFirst() + ";" + timePair.getSecond();
                            })
                            .toList();
            result.addLast(formattedTimes);
        }

        return ResponseEntity.ok(result);
    }

    @Operation(description = "returns information in the list format of String about available time by date")
    @GetMapping("/closest-available-time/{areaId}")
    public Set<String> findAvailableTimeByDate(
            @PathVariable UUID areaId) {
        return bookingMenuService.findClosestAvailableTimes(areaId).stream().map(timePair -> {
            return timePair.getFirst() + ";" + timePair.getSecond();
        }).collect(Collectors.toCollection(TreeSet::new));
    }

    @GetMapping("/available-areas")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "returns information in the list format of UUID about available area on date",
            responses = {
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    public ResponseEntity<List<UUID>> findAvailableAreas(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Set<LocalDateTime> startTimes) {
        if (startTimes == null) {
            startTimes = new HashSet<>();
        }
        List<UUID> availableAreas = bookingMenuService.findAvailableAreas(startTimes);
        return ResponseEntity.ok(availableAreas);
    }

    @Operation(description = "returns information about booking on his id")
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<Booking> getBooking(@PathVariable UUID bookingId) {

        Optional<Booking> booking = bookingMenuService.findBooking(bookingId);
        return booking.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Create a new booking",
            description = "Creates a new booking. Requires a verified user profile.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Booking created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
                    @ApiResponse(responseCode = "403", description = "User profile not verified",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @PostMapping("/booking")
    public Set<ResponseEntity<Booking>> createBooking(@RequestBody CreateBookingRequest request) {
        User currentUser = userService.getCurrentUser();
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
            URI uri = URI.create("/booking-menu/booking/" + b.getId());
            result.add(ResponseEntity.created(uri).body(b));
        }

        return result;
    }

    @Operation(description = "Update booking by id and booking information")
    @PutMapping("/booking/{bookingId}")
    public ResponseEntity<Booking> updateBooking(
            @PathVariable UUID bookingId,
            @RequestBody UpdateBookingRequest request) {
        try {
            User currentUser = userService.getCurrentUser();
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

    @Operation(description = "returns information in the list format in Booking about all bookings")
    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingMenuService.findAll();
        return ResponseEntity.ok(bookings);
    }

    @Operation(description = "create area and returns this area")
    @PostMapping("/area")
    public ResponseEntity<Area> createArea(@RequestBody Area area) {
        area.setStatus(AreaStatus.AVAILABLE);
        areaRepository.save(area);
        URI uri = URI.create("/booking-menu/area/" + area.getId());
        return ResponseEntity.created(uri).body(area);
    }
}