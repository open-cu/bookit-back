package ru.tbank.bookit.book_it_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tbank.bookit.book_it_backend.config.BookingConfig;
import ru.tbank.bookit.book_it_backend.model.Booking;
import ru.tbank.bookit.book_it_backend.model.BookingStatus;
import ru.tbank.bookit.book_it_backend.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingConfig bookingConfig;

    @Autowired
    public BookingService(BookingRepository bookings, BookingConfig bookingConfig) {
        this.bookingRepository = bookings;
        this.bookingConfig = bookingConfig;
    }

    public boolean checkAvailability() {
        return bookingRepository.count() < bookingConfig.getAvailability();
    }

    public boolean setAvailability(int availability) {
        if (availability < 0) {
            throw new IllegalArgumentException("Availability must be non-negative");
        }
        bookingConfig.setAvailability(availability);
        return true;
    }

    public Booking createBooking(Booking booking) {
        if (!checkAvailability()) {
            throw new RuntimeException("No available slots");
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCreatedAt(LocalDateTime.now());
        bookingRepository.save(booking);
        return booking;
    }

    public String getQrCode(long bookingId) {
        if (bookingRepository.findById(bookingId).isEmpty()) {
            throw new RuntimeException("Booking not found");
        }
        return "QR-CODE-FAKE:" + bookingId;
    }

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }
}