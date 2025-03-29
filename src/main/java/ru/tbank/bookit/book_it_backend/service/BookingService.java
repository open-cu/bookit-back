package ru.tbank.bookit.book_it_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tbank.bookit.book_it_backend.config.BookingConfig;
import ru.tbank.bookit.book_it_backend.model.Booking;
import ru.tbank.bookit.book_it_backend.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BookingService {
    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();
    private final BookingConfig bookingConfig;

    @Autowired
    public BookingService(BookingConfig bookingConfig) {
        this.bookingConfig = bookingConfig;
    }

    public boolean checkAvailability() {
        return bookings.size() < bookingConfig.getAvailability();
    }

    public Booking createBooking(Booking booking) {
        if (!checkAvailability()) {
            throw new RuntimeException("No available slots");
        }
        booking.setStatus(BookingStatus.CONFIRMED);
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