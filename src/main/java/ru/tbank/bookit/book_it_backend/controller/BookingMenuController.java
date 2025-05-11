package ru.tbank.bookit.book_it_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import java.util.*;

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
    public List<ResponseEntity<List<String>>> findAvailableTimeByDate(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Optional<UUID> areaId) {

        List<List<Pair<LocalDateTime, LocalDateTime>>> times = bookingMenuService.findAvailableTime(date, areaId);
        List<ResponseEntity<List<String>>> result = new ArrayList<>();

        for (List<Pair<LocalDateTime, LocalDateTime>> l : times) {
            List<String> formattedTimes =
                    l.stream()
                            .map(timePair -> {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                                String startTime = timePair.getFirst().format(formatter);
                                String endTime = timePair.getSecond().format(formatter);
                                return startTime + "-" + endTime;
                            })
                            .toList();
            result.addLast(ResponseEntity.ok(formattedTimes));
        }

        return result;
    }

    @Operation(description = "returns information in the list format of String about available time by date")
    @GetMapping("/closest-available-time/{areaId}")
    public Set<Pair<LocalDateTime, LocalDateTime>> findAvailableTimeByDate(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam UUID areaId) {
        return bookingMenuService.findClosestAvailableTimes(areaId);
    }

    @Operation(description = "returns information in the list format of String about available area on date")
    @GetMapping("/available-areas")
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
    public Set<ResponseEntity<Booking>> createBooking(@RequestBody CreateBookingRequest request) {
        for (Pair<LocalDateTime, LocalDateTime> t : request.getTimePeriods()) {
            if (t.getFirst().isAfter(t.getSecond())) {
                HashSet<ResponseEntity<Booking>> res = new HashSet<>();
                res.add(ResponseEntity.badRequest().build());
                return res;
            }
        }

        Set<Booking> createdBooking = bookingMenuService.createBooking(request);
        Set<ResponseEntity<Booking>> result = new HashSet<>();

        for (Booking b : createdBooking) {
            URI uri = URI.create("/booking-menu/booking/" + b.getId());
            result.add(ResponseEntity.created(uri).body(b));
        }

        return result;
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