package com.opencu.bookit.adapter.in.web.controller;

import com.opencu.bookit.adapter.in.web.dto.request.CreateBookingRequest;
import com.opencu.bookit.adapter.in.web.dto.request.UpdateBookingRequest;
import com.opencu.bookit.adapter.in.web.dto.response.BookingResponse;
import com.opencu.bookit.adapter.in.web.exception.ApiError;
import com.opencu.bookit.adapter.in.web.exception.ProfileNotCompletedException;
import com.opencu.bookit.adapter.in.web.mapper.BookingRequestMapper;
import com.opencu.bookit.adapter.in.web.mapper.BookingResponseMapper;
import com.opencu.bookit.application.port.in.booking.CRUDBookingUseCase;
import com.opencu.bookit.application.port.out.user.LoadAuthorizationInfoPort;
import com.opencu.bookit.application.service.booking.BookingService;
import com.opencu.bookit.domain.model.booking.BookingModel;
import com.opencu.bookit.domain.model.user.UserModel;
import com.opencu.bookit.domain.model.user.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.util.Pair;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/booking-menu")
public class BookingController {
    private final BookingService bookingService;
    private final BookingRequestMapper bookingRequestMapper;
    private final BookingResponseMapper bookingResponseMapper;
    private final LoadAuthorizationInfoPort loadAuthorizationInfoPort;

    public BookingController(BookingService bookingService,
                             BookingRequestMapper bookingRequestMapper, BookingResponseMapper bookingResponseMapper,
                             LoadAuthorizationInfoPort loadAuthorizationInfoPort) {
        this.bookingService = bookingService;
        this.bookingRequestMapper = bookingRequestMapper;
        this.bookingResponseMapper = bookingResponseMapper;
        this.loadAuthorizationInfoPort = loadAuthorizationInfoPort;
    }

    @Operation(description = "Returns information in the list format about the available dates")
    @GetMapping("/available-date")
    public List<LocalDate> findAvailableDates(@RequestParam Optional<UUID> areaId) {
        return bookingService.findAvailableDates(areaId);
    }

    @Operation(description = "Returns list of available time by startTime separated by ; (start_time;end_time)")
    @GetMapping("/available-time/{date}")
    public ResponseEntity<List<List<String>>> findAvailableTimeByDate(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Optional<UUID> areaId,
            @RequestParam(required = false) Optional<UUID> bookingId) {

        List<List<Pair<LocalDateTime, LocalDateTime>>> times = bookingService.findAvailableTime(date, areaId, bookingId);
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

    @Operation(description = "Returns information in the list format of String about available time by startTime")
    @GetMapping("/closest-available-time/{areaId}")
    public Set<String> findAvailableTimeByDate(
            @PathVariable UUID areaId) {
        return bookingService.findClosestAvailableTimes(areaId).stream().map(timePair -> {
            return timePair.getFirst() + ";" + timePair.getSecond();
        }).collect(Collectors.toCollection(TreeSet::new));
    }

    @GetMapping("/available-areas")
    @Operation(
            summary = "Returns information in the list format of UUID about available area on startTime"
    )
    public ResponseEntity<List<UUID>> findAvailableAreas(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Set<LocalDateTime> startTimes) {
        if (startTimes == null) {
            startTimes = new HashSet<>();
        }
        List<UUID> availableAreas = bookingService.findAvailableAreas(startTimes);
        return ResponseEntity.ok(availableAreas);
    }

    @Operation(description = "Returns information about booking on his id")
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable UUID bookingId) {

        Optional<BookingModel> booking = bookingService.findBooking(bookingId);
        return booking.map(bookingResponseMapper::toResponse).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
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
    public List<ResponseEntity<BookingResponse>> createBooking(@RequestBody CreateBookingRequest request) {
        UserModel currentUser = loadAuthorizationInfoPort.getCurrentUser();
        if (currentUser.getStatus() != UserStatus.VERIFIED) {
            throw new ProfileNotCompletedException("User profile is not completed. Please complete your profile before creating bookings.");
        }

        for (Pair<LocalDateTime, LocalDateTime> t : request.timePeriods()) {
            if (t.getFirst().isAfter(t.getSecond())) {
                List<ResponseEntity<BookingResponse>> res = new ArrayList<>();
                res.add(ResponseEntity.badRequest().build());
                return res;
            }
        }

        CRUDBookingUseCase.CreateBookingCommand actualRequest = new CRUDBookingUseCase.CreateBookingCommand(
                currentUser.getId(),
                request.areaId(),
                request.timePeriods(),
                request.quantity()
        );

        List<BookingModel> createdBooking = bookingService.createBooking(actualRequest, false, false);
        List<ResponseEntity<BookingResponse>> result = new ArrayList<>();

        for (BookingModel b : createdBooking) {
            URI uri = URI.create("/booking-menu/booking/" + b.getId());
            result.add(ResponseEntity.created(uri).body(bookingResponseMapper.toResponse(b)));
        }

        return result;
    }

    @Operation(description = "Update booking by id and booking information")
    @PutMapping("/booking/{bookingId}")
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable UUID bookingId,
            @RequestBody UpdateBookingRequest request) {
        try {
            UserModel currentUser = loadAuthorizationInfoPort.getCurrentUser();
            if (currentUser.getStatus() != UserStatus.VERIFIED) {
                throw new ProfileNotCompletedException("User profile is not completed. Please complete your profile before updating bookings.");
            }

            BookingModel updatedBooking = bookingService.updateBooking(bookingId, bookingRequestMapper.toQuery(request));
            return ResponseEntity.ok(bookingResponseMapper.toResponse(updatedBooking));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (ProfileNotCompletedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @Operation(description = "Returns information in the list format in Booking about all bookings")
    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List<BookingModel> bookings = bookingService.findAll();
        return ResponseEntity.ok(bookingResponseMapper.toResponseList(bookings));
    }
}