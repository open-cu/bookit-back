package ru.tbank.bookit.book_it_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.tbank.bookit.book_it_backend.exception.ProfileNotCompletedException;
import ru.tbank.bookit.book_it_backend.model.Booking;
import ru.tbank.bookit.book_it_backend.model.User;
import ru.tbank.bookit.book_it_backend.model.UserStatus;
import ru.tbank.bookit.book_it_backend.security.services.UserDetailsImpl;
import ru.tbank.bookit.book_it_backend.service.HomeService;

import java.util.*;

@RestController
@RequestMapping("/home")
public class HomeController {
    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @Operation(description = "returns QR code in string format")
    @GetMapping("/qr")
    public ResponseEntity<byte[]> getUserQrCode() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = homeService.findUserByTgId(userDetails.getTgId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getStatus() != UserStatus.VERIFIED) {
            throw new ProfileNotCompletedException("User profile is not completed. Please complete your profile before accessing QR code.");
        }

        try {
            byte[] qrPng = homeService.generateUserQrCode(user);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"user-" + user.getId() + "-qr.png\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrPng);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate QR code", e);
        }
    }

    @Operation(description = "returns information in the list format about current bookings")
    @GetMapping("/bookings/current")
    public ResponseEntity<List<Booking>> getCurrentBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = userDetails.getId();
        List<Booking> bookings = new ArrayList<>(homeService.getCurrentBookings(userId));
        bookings.sort(Comparator.comparing(Booking::getStartTime));
        return ResponseEntity.ok(bookings);
    }

    @Operation(description = "returns information in the list format about future bookings")
    @GetMapping("/bookings/future")
    public ResponseEntity<List<Booking>> getFutureBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = userDetails.getId();
        List<Booking> bookings = new ArrayList<>(homeService.getFutureBookings(userId));
        bookings.sort(Comparator.comparing(Booking::getStartTime));
        return ResponseEntity.ok(bookings);
    }

    @Operation(description = "returns information in the list format about past bookings")
    @GetMapping("/bookings/past")
    public ResponseEntity<List<Booking>> getPastBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = userDetails.getId();
        List<Booking> bookings = new ArrayList<>(homeService.getPastBookings(userId));
        bookings.sort(Comparator.comparing(Booking::getStartTime));
        return ResponseEntity.ok(bookings);
    }

    @Operation(description = "returns a string about successful remote booking")
    @DeleteMapping("/booking/{bookingId}")
    public ResponseEntity<String> cancelBooking(@PathVariable UUID bookingId) {
        homeService.cancelBooking(bookingId);
        return ResponseEntity.ok("Booking cancelled successfully");
    }

    @Operation(description = "returns in format UUID a userId")
    @GetMapping("/testUserId")
    public ResponseEntity<UUID> getTestUserId() {
        UUID testUserId = homeService.getTestUserId();
        return ResponseEntity.ok(testUserId);
    }

    @Operation(description = "returns in format String a area name")
    @GetMapping("/area-name/{areaId}")
    public ResponseEntity<String> getAreaName(@PathVariable UUID areaId) {
        String areaName = homeService.findAreaNameById(areaId);
        return ResponseEntity.ok(areaName);
    }
}