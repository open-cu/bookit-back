package ru.tbank.bookit.book_it_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.bookit.book_it_backend.service.StatsService;
import ru.tbank.bookit.book_it_backend.DTO.BookingStatsResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService bookingStatsService;

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingStatsResponse>> getBookingStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {

        List<BookingStatsResponse> stats = bookingStatsService
                .getBookingStats(startDate, endDate);

        return ResponseEntity.ok(stats);
    }
}
