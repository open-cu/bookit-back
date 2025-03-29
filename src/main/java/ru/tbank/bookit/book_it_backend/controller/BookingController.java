package ru.tbank.bookit.book_it_backend.controller;

import org.springframework.web.bind.annotation.*;
import ru.tbank.bookit.book_it_backend.model.Booking;
import ru.tbank.bookit.book_it_backend.service.BookingService;

@RestController
@RequestMapping("/booking")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/availability")
    public boolean checkAvailability() {
        return bookingService.checkAvailability();
    }

    @PostMapping("/availability")
    public boolean setAvailability(@RequestParam int availability) {
        return bookingService.setAvailability(availability);
    }

    @PostMapping("/book")
    public Booking createBooking(@RequestBody Booking booking) {
        return bookingService.createBooking(booking);
    }

    @GetMapping("/qr/{bookingId}")
    public String getQrCode(@PathVariable String bookingId) {
        return bookingService.getQrCode(bookingId);
    }
}