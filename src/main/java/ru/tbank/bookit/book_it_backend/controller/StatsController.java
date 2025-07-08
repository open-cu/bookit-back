package ru.tbank.bookit.book_it_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.tbank.bookit.book_it_backend.model.StatsPeriod;
import ru.tbank.bookit.book_it_backend.service.StatsService;
import ru.tbank.bookit.book_it_backend.DTO.*;
import ru.tbank.bookit.book_it_backend.service.UserService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @Operation(description = "Returns booking statistics for the specified date range")
    @GetMapping("/bookings")
    public ResponseEntity<FullStatsResponse> getBookingStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "false") Boolean includeSummary) {

        if (startDate.isAfter(endDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date");
        }

        try {
            List<BookingStatsResponse> stats = statsService.getBookingStats(startDate, endDate);
            StatsSummaryResponse summary = includeSummary
                    ? statsService.getStatsSummary(startDate, endDate, stats)
                    : null;

            return ResponseEntity.ok(new FullStatsResponse(stats, summary));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve booking statistics", e);
        }
    }

    @Operation(description = "Returns booking statistics for the specified period (week, fortnight, month, quarter)")
    @GetMapping("/bookings-period")
    public ResponseEntity<FullStatsResponse> getBookingStatsByPeriod(
            @RequestParam String period,
            @RequestParam(required = false, defaultValue = "false") Boolean includeSummary) {

        if (period == null || period.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Period parameter cannot be null or empty");
        }

        final StatsPeriod statsPeriod;
        try {
            statsPeriod = StatsPeriod.fromString(period.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid period value. Valid values are: " + Arrays.toString(StatsPeriod.values()));
        }

        final LocalDate endDate = LocalDate.now();
        final LocalDate startDate = endDate.minusWeeks(statsPeriod.getWeeksCount());

        try {
            List<BookingStatsResponse> stats = statsService.getBookingStats(startDate, endDate);
            StatsSummaryResponse summary = includeSummary
                    ? statsService.getStatsSummary(startDate, endDate, stats)
                    : null;

            return ResponseEntity.ok(new FullStatsResponse(stats, summary));

        } catch (DataAccessException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Database access error");
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    ex.getMessage());
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Internal server error");
        }
    }

    @Operation(description = "Returns booking statistics grouped by day of week")
    @GetMapping("/bookings-by-day-of-week")
    public ResponseEntity<List<DayOfWeekStatsResponse>> getBookingStatsByDayOfWeek(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate.isAfter(endDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date");
        }

        try {
            return ResponseEntity.ok(statsService.getBookingStatsByDayOfWeek(startDate, endDate));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve booking statistics", e);
        }
    }

    @Operation(description = "Returns cancellation statistics by area")
    @GetMapping("/cancellations-by-area")
    public ResponseEntity<List<CancellationStatsResponse>> getCancellationStatsByArea(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate.isAfter(endDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date");
        }

        try {
            return ResponseEntity.ok(statsService.getCancellationStatsByArea(startDate, endDate));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve cancellation statistics", e);
        }
    }

    @Operation(description = "Returns busiest hours statistics for visualization")
    @GetMapping("/busiest-hours")
    public ResponseEntity<List<BusiestHoursResponse>> getBusiestHours(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String areaName) {

        if (startDate.isAfter(endDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Start date must be before end date");
        }

        try {
            List<BusiestHoursResponse> stats;
            if (areaName == null) {
                stats = statsService.getBusiestHoursForHall(startDate, endDate);
            } else {
                stats = statsService.getBusiestHoursStats(
                        startDate.atStartOfDay(),
                        endDate.atTime(23, 59, 59),
                        areaName);
            }
            return ResponseEntity.ok(stats);
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Data service unavailable");
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    e.getMessage());
        }
    }
}