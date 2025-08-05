package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.application.service.statistics.StatsService;
import com.opencu.bookit.domain.model.statistics.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
public class StatsControllerV1 {

    private final StatsService statsService;
    @Value("${booking.zone-id}")
    private ZoneId zoneId;

    @Operation(description = "Returns booking statistics for the specified date range")
    @GetMapping("/bookings")
    public ResponseEntity<FullStats> getBookingStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "false") Boolean includeSummary,
            @RequestParam(required = false) List<String> areaNames) {

        if (startDate.isAfter(endDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date");
        }

        try {
            List<BookingStats> stats = statsService.getBookingStats(startDate, endDate, areaNames);
            StatsSummary summary = includeSummary
                    ? statsService.getStatsSummary(startDate, endDate, stats)
                    : null;

            return ResponseEntity.ok(new FullStats(stats, summary));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve booking statistics", e);
        }
    }

    @Operation(description = "Returns booking statistics for the specified period (week, fortnight, month, quarter)")
    @GetMapping("/bookings-period")
    public ResponseEntity<FullStats> getBookingStatsByPeriod(
            @RequestParam String period,
            @RequestParam(required = false, defaultValue = "false") Boolean includeSummary,
            @RequestParam(required = false) List<String> areaNames) {

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

        final LocalDate endDate = LocalDate.now(zoneId);
        final LocalDate startDate = endDate.minusWeeks(statsPeriod.getWeeksCount());

        try {
            List<BookingStats> stats = statsService.getBookingStats(startDate, endDate, areaNames);
            StatsSummary summary = includeSummary
                    ? statsService.getStatsSummary(startDate, endDate, stats)
                    : null;

            return ResponseEntity.ok(new FullStats(stats, summary));

        } catch (DataAccessException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    ex.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    ex.getMessage());
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    ex.getMessage());
        }
    }

    @Operation(description = "Returns booking statistics grouped by day of week")
    @GetMapping("/bookings-by-day-of-week")
    public ResponseEntity<List<DayOfWeekStats>> getBookingStatsByDayOfWeek(
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
    public ResponseEntity<List<CancellationStats>> getCancellationStatsByArea(
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
    public ResponseEntity<List<BusiestHours>> getBusiestHours(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String areaName) {

        if (startDate.isAfter(endDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Start date must be before end date");
        }

        try {
            List<BusiestHours> stats;
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
                    e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    e.getMessage());
        }
    }

    @GetMapping("/event-overlaps")
    public ResponseEntity<List<EventOverlap>> getEventOverlaps(
            @RequestParam(required = false) UUID eventId1,
            @RequestParam(required = false) UUID eventId2) {
        return ResponseEntity.ok(statsService.eventOverlapStats(eventId1, eventId2));
    }

    @GetMapping("/new-users-by-year-month")
    @Operation(
            summary = "Get statistics about new users by year and month",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = NewUsersCreatedAt.class)
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Example of successful response",
                                                    value = """
                                                            [
                                                                  {
                                                                      "created": "2025-04",
                                                                      "count": 3
                                                                  },
                                                                  {
                                                                      "created": "2025-07",
                                                                      "count": 1
                                                                  }
                                                            ]
                                                            """
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<List<NewUsersCreatedAt>> getNewUsersByYearMonth() {
        return ResponseEntity.ok(statsService.newUsersCreatedAtStats());
    }
}