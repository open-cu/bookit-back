package ru.tbank.bookit.book_it_backend.service;

import org.springframework.stereotype.Service;
import ru.tbank.bookit.book_it_backend.model.Booking;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BookingService {
    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();
    public static final int AVAILABILITY = 10;

    public boolean checkAvailability() {
        return bookings.size() < AVAILABILITY;
    }

    public Booking createBooking(Booking booking) {
        if (!checkAvailability()) {
            throw new RuntimeException("No available slots");
        }
        booking.setId(UUID.randomUUID().toString());
        booking.setStatus("CONFIRMED");
        booking.setCreatedAt(LocalDateTime.now());
        bookings.put(booking.getId(), booking);
        return booking;
    }

    public String getQrCode(String bookingId) {
        if (!bookings.containsKey(bookingId)) {
            throw new RuntimeException("Booking not found");
        }
        return "QR-CODE-FAKE:" + bookingId;
    }
}
