package ru.tbank.bookit.book_it_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.util.Pair;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.bookit.book_it_backend.DTO.CreateBookingRequest;
import ru.tbank.bookit.book_it_backend.exception.ApiError;
import ru.tbank.bookit.book_it_backend.DTO.UpdateBookingRequest;
import ru.tbank.bookit.book_it_backend.model.Area;
import ru.tbank.bookit.book_it_backend.model.AreaStatus;
import ru.tbank.bookit.book_it_backend.model.Booking;
import ru.tbank.bookit.book_it_backend.repository.AreaRepository;
import ru.tbank.bookit.book_it_backend.service.BookingMenuService;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/booking-menu")
public class BookingMenuController {
    private final BookingMenuService bookingMenuService;
    private final AreaRepository areaRepository;

    public BookingMenuController(BookingMenuService bookingMenuService, AreaRepository areaRepository) {
        this.bookingMenuService = bookingMenuService;
        this.areaRepository = areaRepository;
    }

    @Operation(description = "returns information in the list format about the available dates")
    @GetMapping("/available-date")
    public List<LocalDate> findAvailableDates(@RequestParam Optional<UUID> areaId) {
        return bookingMenuService.findAvailableDates(areaId);
    }


    @Operation(description = "returns information in the list format of String about available time by date")
    @GetMapping("/available-time/{date}")
    public ResponseEntity<List<String>> findAvailableTimeByDate(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Optional<UUID> areaId) {

        List<Pair<LocalDateTime, LocalDateTime>> times = bookingMenuService.findAvailableTime(date, areaId);
        List<String> formattedTimes =
                times.stream()
                     .map(timePair -> {
                         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                         String startTime = timePair.getFirst().format(formatter);
                         String endTime = timePair.getSecond().format(formatter);
                         return startTime + "-" + endTime;
                     })
                     .toList();
        return ResponseEntity.ok(formattedTimes);
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
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {
        List<UUID> availableArea = bookingMenuService.findAvailableAreas(time);
        return ResponseEntity.ok(availableArea);
    }

    @Operation(description = "returns information about booking on his id")
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<Booking> getBooking(@PathVariable UUID bookingId) {

        Optional<Booking> booking = bookingMenuService.findBooking(bookingId);
        return booking.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(description = "returns information about the created booking, and the method accepts the booking itself, which must be added.")
    @PostMapping("/booking")
    public ResponseEntity<Booking> createBooking(@RequestBody CreateBookingRequest request) {
        if (request.getStartTime() == null || request.getEndTime() == null ||
                request.getStartTime().isAfter(request.getEndTime())) {
            return ResponseEntity.badRequest().build();
        }

        Booking createdBooking = bookingMenuService.createBooking(request);
        URI uri = URI.create("/booking-menu/booking/" + createdBooking.getId());
        return ResponseEntity.created(uri).body(createdBooking);
    }

    @Operation(description = "changes existing Booking and returns this Booking")
    @PutMapping("/booking/{bookingId}")
    public ResponseEntity<Booking> updateBooking(
            @PathVariable UUID bookingId,
            @RequestBody UpdateBookingRequest request) {
        try {
            Optional<Booking> existingBooking = bookingMenuService.findBooking(bookingId);
            if (existingBooking.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Booking booking = existingBooking.get();

            UUID areaId = request.areaId() != null ? request.areaId() : booking.getAreaId();
            LocalDateTime startTime = request.startTime() != null ? request.startTime() : booking.getStartTime();
            LocalDateTime endTime = request.endTime() != null ? request.endTime() : booking.getEndTime();

            if (startTime.isAfter(endTime)) {
                return ResponseEntity.badRequest().build();
            }

            Booking updatedBooking = bookingMenuService.updateBooking(bookingId, areaId, startTime, endTime);
            return ResponseEntity.ok(updatedBooking);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(description = "returns information in the list format in Booking about all bookings")
    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingMenuService.findAll();
        return ResponseEntity.ok(bookings);
    }

    @Operation(description = "cancellation of the booking, returns a string about the success of the deletion")
    @DeleteMapping("/booking/{bookingId}")
    public ResponseEntity<String> deleteBooking(@PathVariable UUID bookingId) {
        bookingMenuService.deleteBooking(bookingId);
        return ResponseEntity.ok("Booking canceled successfully");
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