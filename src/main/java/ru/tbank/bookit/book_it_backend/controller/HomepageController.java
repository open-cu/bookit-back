package ru.tbank.bookit.book_it_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.tbank.bookit.book_it_backend.model.Booking;
import ru.tbank.bookit.book_it_backend.model.User;
import ru.tbank.bookit.book_it_backend.repository.UserRepository;
import ru.tbank.bookit.book_it_backend.service.HomepageService;

import java.util.List;

@RestController
@RequestMapping("/homepage")
public class HomepageController {
    private final HomepageService homepageService;
    private final UserRepository userRepository;

    @Autowired
    public HomepageController(HomepageService homepageService, UserRepository userRepository) {
        this.homepageService = homepageService;
        this.userRepository = userRepository;
    }

    @PostMapping("/create-booking")
    public Booking createBooking(@RequestBody Booking booking) {
        return homepageService.createBooking(booking);
    }

    @GetMapping("/qr")
    public ResponseEntity<?> getUserQrCode(@RequestParam Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        try {
            String qrContent = homepageService.generateUserQrCode(user);
            return ResponseEntity.ok(qrContent);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate QR code", e);
        }
    }

    @GetMapping("/bookings/current")
    public ResponseEntity<List<Booking>> getCurrentBookings() {
        List<Booking> bookings = homepageService.getCurrentBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/bookings/future")
    public ResponseEntity<List<Booking>> getFutureBookings() {
        List<Booking> bookings = homepageService.getFutureBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/bookings/past")
    public ResponseEntity<List<Booking>> getPastBookings() {
        List<Booking> bookings = homepageService.getPastBookings();
        return ResponseEntity.ok(bookings);
    }

    @DeleteMapping("/cancel-booking/{bookingId}")
    public ResponseEntity<String> cancelBooking(@PathVariable long bookingId) {
        homepageService.cancelBooking(bookingId);
        return ResponseEntity.ok("Booking cancelled successfully");
    }
}