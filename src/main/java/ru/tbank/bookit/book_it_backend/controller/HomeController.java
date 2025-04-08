package ru.tbank.bookit.book_it_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
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
    private final UserRepository userRepository;

    @Autowired
    public HomeController(HomeService homeService, UserRepository userRepository) {
        this.homeService = homeService;
        this.userRepository = userRepository;
    }

    @Operation(
            description = "returns QR code in string format"
    )
    @GetMapping("/qr")
    public ResponseEntity<?> getUserQrCode(@RequestParam Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        try {
            String qrContent = homeService.generateUserQrCode(user);
            return ResponseEntity.ok(qrContent);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate QR code", e);
        }
    }

    @Operation(
            description = "returns information in the list format about current bookings"
    )
    @GetMapping("/bookings/current")
    public ResponseEntity<List<Booking>> getCurrentBookings(@RequestParam Long userId) {
        List<Booking> bookings = homeService.getCurrentBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @Operation(
            description = "returns information in the list format about future bookings"
    )
    @GetMapping("/bookings/future")
    public ResponseEntity<List<Booking>> getFutureBookings(@RequestParam Long userId) {
        List<Booking> bookings = homeService.getFutureBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @Operation(
            description = "returns information in the list format about past bookings"
    )
    @GetMapping("/bookings/past")
    public ResponseEntity<List<Booking>> getPastBookings(@RequestParam Long userId) {
        List<Booking> bookings = homeService.getPastBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @Operation(
            description = "returns a string about successful remote booking"
    )
    @DeleteMapping("/booking/{bookingId}")
    public ResponseEntity<String> cancelBooking(@PathVariable long bookingId) {
        homeService.cancelBooking(bookingId);
        return ResponseEntity.ok("Booking cancelled successfully");
    }
}