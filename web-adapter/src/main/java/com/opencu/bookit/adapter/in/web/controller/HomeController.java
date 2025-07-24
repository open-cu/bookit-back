package com.opencu.bookit.adapter.in.web.controller;

import com.opencu.bookit.adapter.in.web.dto.response.BookingResponse;
import com.opencu.bookit.adapter.in.web.exception.ProfileNotCompletedException;
import com.opencu.bookit.adapter.in.web.mapper.BookingResponseMapper;
import com.opencu.bookit.adapter.out.security.spring.service.UserDetailsImpl;
import com.opencu.bookit.application.port.out.qr.GenerateQrCodePort;
import com.opencu.bookit.application.service.area.AreaService;
import com.opencu.bookit.application.service.booking.BookingService;
import com.opencu.bookit.application.service.user.UserService;
import com.opencu.bookit.domain.model.booking.BookingModel;
import com.opencu.bookit.domain.model.user.UserModel;
import com.opencu.bookit.domain.model.user.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/home")
public class HomeController {
    private final UserService userService;
    private final GenerateQrCodePort generateQrCodePort;
    private final AreaService areaService;
    private final BookingService bookingService;
    private final BookingResponseMapper bookingResponseMapper;

    @Autowired
    public HomeController(UserService userService, GenerateQrCodePort generateQrCodePort, AreaService areaService, BookingService bookingService,
                          BookingResponseMapper bookingResponseMapper) {
        this.userService = userService;
        this.generateQrCodePort = generateQrCodePort;
        this.areaService = areaService;
        this.bookingService = bookingService;
        this.bookingResponseMapper = bookingResponseMapper;
    }

    @Operation(description = "Returns QR code in string format")
    @GetMapping("/qr")
    public ResponseEntity<byte[]> getUserQrCode() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserModel user = userService.findByTgId(userDetails.getTgId())
                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getStatus() != UserStatus.VERIFIED) {
            throw new ProfileNotCompletedException("User profile is not completed. Please complete your profile before accessing QR code.");
        }

        try {
            byte[] qrPng = generateQrCodePort.generateUserQrCode(user);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"user-" + user.getId() + "-qr.png\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrPng);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate QR code", e);
        }
    }

    @Operation(description = "Returns information in the list format about current bookings")
    @GetMapping("/bookings/current")
    public ResponseEntity<List<BookingResponse>> getCurrentBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = userDetails.getId();
        List<BookingModel> bookings = new ArrayList<>(bookingService.getCurrentBookings(userId));
        bookings.sort(Comparator.comparing(BookingModel::getStartTime));
        return ResponseEntity.ok(bookingResponseMapper.toResponseList(bookings));
    }

    @Operation(description = "Returns information in the list format about future bookings")
    @GetMapping("/bookings/future")
    public ResponseEntity<List<BookingResponse>> getFutureBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = userDetails.getId();
        List<BookingModel> bookings = new ArrayList<>(bookingService.getFutureBookings(userId));
        bookings.sort(Comparator.comparing(BookingModel::getStartTime));
        return ResponseEntity.ok(bookingResponseMapper.toResponseList(bookings));
    }

    @Operation(description = "Returns information in the list format about past bookings")
    @GetMapping("/bookings/past")
    public ResponseEntity<List<BookingResponse>> getPastBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = userDetails.getId();
        List<BookingModel> bookings = new ArrayList<>(bookingService.getPastBookings(userId));
        bookings.sort(Comparator.comparing(BookingModel::getStartTime));
        return ResponseEntity.ok(bookingResponseMapper.toResponseList(bookings));
    }

    @Operation(description = "Returns a string about successful remote booking")
    @DeleteMapping("/booking/{bookingId}")
    public ResponseEntity<String> cancelBooking(@PathVariable UUID bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok("Booking cancelled successfully");
    }

    @Operation(description = "Returns in format UUID a userId")
    @GetMapping("/testUserId")
    public ResponseEntity<UUID> getTestUserId() {
        UUID testUserId = userService.getTestUserId();
        return ResponseEntity.ok(testUserId);
    }

    @Operation(description = "Returns in format String a area name")
    @GetMapping("/area-name/{areaId}")
    public ResponseEntity<String> getAreaName(@PathVariable UUID areaId) {
        String areaName = areaService.findAreaNameById(areaId);
        return ResponseEntity.ok(areaName);
    }
}