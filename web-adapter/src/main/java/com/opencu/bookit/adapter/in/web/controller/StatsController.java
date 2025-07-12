package com.opencu.bookit.adapter.in.web.controller;

import com.opencu.bookit.domain.model.statistics.BookingStats;
import com.opencu.bookit.application.service.statistics.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService bookingStatsService;

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingStats>> getBookingStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
                                                             ) {

        List<BookingStats> stats = bookingStatsService
                .getBookingStats(startDate, endDate);

        return ResponseEntity.ok(stats);
    }
}
