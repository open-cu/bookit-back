package ru.tbank.bookit.book_it_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

@RestController
@RequestMapping("/booking-menu")
public class BookingMenuController {
    private final BookingMenuService bookingMenuService;
    private final AreaRepository areaRepository;

    public BookingMenuController(BookingMenuService bookingMenuService, AreaRepository areaRepository) {
        this.bookingMenuService = bookingMenuService;
        this.areaRepository = areaRepository;
    }

    @GetMapping("/available-date")
    public List<LocalDate> findAvailableDates(@RequestParam Optional<String> areaId) {
        return bookingMenuService.findAvailableDates(areaId);
    }

    @GetMapping("/available-time/{date}")
    public ResponseEntity<List<String>> findAvailableTimeByDate(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Optional<String> areaId) {

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

    @GetMapping("/available-area")
    public ResponseEntity<List<String>> findAvailableArea(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {
        List<String> availableArea = bookingMenuService.findAvailableArea(time);
        return ResponseEntity.ok(availableArea);
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<Booking> getBooking(@PathVariable long bookingId) {
        if (bookingId <= 0) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Booking> booking = bookingMenuService.findBooking(bookingId);
        return booking.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/booking")
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        if (booking.getStartTime() == null || booking.getEndTime() == null ||
                booking.getStartTime().isAfter(booking.getEndTime())) {
            return ResponseEntity.badRequest().build();
        }

        Booking createdBooking = bookingMenuService.createBooking(booking);
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