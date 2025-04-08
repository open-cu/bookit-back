package ru.tbank.bookit.book_it_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.tbank.bookit.book_it_backend.model.Booking;
import ru.tbank.bookit.book_it_backend.model.User;
import ru.tbank.bookit.book_it_backend.repository.UserRepository;
import ru.tbank.bookit.book_it_backend.service.HomeService;

import java.util.List;

@RestController
@RequestMapping("/home")
public class HomeController {
    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping("/qr")
    public ResponseEntity<?> getUserQrCode(@RequestParam Long userId) {
        User user = homeService.findUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        try {
            String qrContent = homeService.generateUserQrCode(user);
            return ResponseEntity.ok(qrContent);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate QR code", e);
        }
    }

    @GetMapping("/bookings/current")
    public ResponseEntity<List<Booking>> getCurrentBookings(@RequestParam Long userId) {
        List<Booking> bookings = homeService.getCurrentBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/bookings/future")
    public ResponseEntity<List<Booking>> getFutureBookings(@RequestParam Long userId) {
        List<Booking> bookings = homeService.getFutureBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/bookings/past")
    public ResponseEntity<List<Booking>> getPastBookings(@RequestParam Long userId) {
        List<Booking> bookings = homeService.getPastBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @DeleteMapping("/booking/{bookingId}")
    public ResponseEntity<String> cancelBooking(@PathVariable long bookingId) {
        homeService.cancelBooking(bookingId);
        return ResponseEntity.ok("Booking cancelled successfully");
    }
}