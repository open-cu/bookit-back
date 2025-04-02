package ru.tbank.bookit.book_it_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.bookit.book_it_backend.model.Booking;
import ru.tbank.bookit.book_it_backend.service.BookingMenuService;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/booking-menu")
public class BookingMenuController {
    private final BookingMenuService bookingMenuService;

    @Autowired
    public BookingMenuController(BookingMenuService bookingMenuService) {
        this.bookingMenuService = bookingMenuService;
    }

    @GetMapping("/available-date")
    public List<LocalDate> findAvailableDate(@RequestParam Optional<String> areaId) {
        return bookingMenuService.findAvailableDate();
    }

    @GetMapping("/available-time/{date}")
    public ResponseEntity<List<String>> findAvailableTimeByDate(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Optional<String> areaId) {

        List<LocalDateTime> times = bookingMenuService.findAvailableTime(date, areaId);
        List<String> formattedTimes = times.stream()
                                           .map(time -> time.format(DateTimeFormatter.ofPattern("HH:mm")))
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
        Booking booking = bookingMenuService.findBooking(bookingId);
        if (booking == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/booking")
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        if ((booking.getStartTime() == null || booking.getEndTime() == null) ||
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
}