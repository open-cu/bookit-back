package ru.tbank.bookit.book_it_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.tbank.bookit.book_it_backend.model.Booking;
import ru.tbank.bookit.book_it_backend.model.User;
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
    public ResponseEntity<byte[]> getUserQrCode(@RequestParam UUID userId) {
        User user = homeService.findUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        try {
            byte[] qrPng = homeService.generateUserQrCode(user);
            return ResponseEntity.ok()
                                 .header(HttpHeaders.CONTENT_DISPOSITION,
                                         "inline; filename=\"user-" + userId + "-qr.png\"")
                                 .contentType(MediaType.IMAGE_PNG)
                                 .body(qrPng);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate QR code", e);
        }
    }

    @Operation(description = "returns information in the list format about current bookings")
    @GetMapping("/bookings/current")
    public ResponseEntity<List<Booking>> getCurrentBookings(@RequestParam UUID userId) {
        List<Booking> bookings = new ArrayList<Booking>(homeService.getPastBookings(userId));
        bookings.sort(Comparator.comparing(Booking::getStartTime));
        return ResponseEntity.ok(bookings);
    }

    @Operation(description = "returns information in the list format about future bookings")
    @GetMapping("/bookings/future")
    public ResponseEntity<List<Booking>> getFutureBookings(@RequestParam UUID userId) {
        List<Booking> bookings = new ArrayList<Booking>(homeService.getFutureBookings(userId));
        bookings.sort(Comparator.comparing(Booking::getStartTime));
        return ResponseEntity.ok(bookings);
    }

    @Operation(description = "returns information in the list format about past bookings")
    @GetMapping("/bookings/past")
    public ResponseEntity<List<Booking>> getPastBookings(@RequestParam UUID userId) {
        List<Booking> bookings = new ArrayList<Booking>(homeService.getPastBookings(userId));
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