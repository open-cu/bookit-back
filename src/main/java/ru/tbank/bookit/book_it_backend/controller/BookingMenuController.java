package ru.tbank.bookit.book_it_backend.controller;

import org.springframework.data.util.Pair;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.bookit.book_it_backend.DTO.CreateBookingRequest;
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

    @GetMapping("/available-dates")
    public List<LocalDate> findAvailableDates(@RequestParam Optional<UUID> areaId) {
        return bookingMenuService.findAvailableDates(areaId);
    }

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
    public ResponseEntity<List<UUID>> findAvailableAreas(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {
        List<UUID> availableArea = bookingMenuService.findAvailableAreas(time);
        return ResponseEntity.ok(availableArea);
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<Booking> getBooking(@PathVariable UUID bookingId) {

        Optional<Booking> booking = bookingMenuService.findBooking(bookingId);
        return booking.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

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

    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingMenuService.findAll();
        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/area")
    public ResponseEntity<Area> createArea(@RequestBody Area area) {
        area.setStatus(AreaStatus.AVAILABLE);
        areaRepository.save(area);
        URI uri = URI.create("/booking-menu/area/" + area.getId());
        return ResponseEntity.created(uri).body(area);
    }
}