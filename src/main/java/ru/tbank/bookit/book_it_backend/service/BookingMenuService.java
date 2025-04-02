package ru.tbank.bookit.book_it_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.tbank.bookit.book_it_backend.config.BookingConfig;
import ru.tbank.bookit.book_it_backend.model.Booking;
import ru.tbank.bookit.book_it_backend.model.BookingStatus;
import ru.tbank.bookit.book_it_backend.repository.BookingRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingMenuService {
    private final BookingRepository bookingRepository;
    private final BookingConfig bookingConfig;

    @Autowired
    public BookingMenuService(BookingRepository bookings, BookingConfig bookingConfig) {
        this.bookingRepository = bookings;
        this.bookingConfig = bookingConfig;
    }

    public Booking findBooking(long bookingId) {
        Booking booking = bookingRepository.findBookingById(bookingId);
        return booking;
    }

    public List<LocalDate> findAvailableDate() {
        List<LocalDate> availableDates = List.of();
        return availableDates;
    }

    public List<Pair<LocalDateTime, LocalDateTime>> findAvailableTime(LocalDate date, Optional<String> areaId) {
        List<Pair<LocalDateTime, LocalDateTime>> availableTime = List.of();
        return availableTime;
    }

    public List<String> findAvailableArea(LocalDateTime time) {
        List<String> availableArea = List.of();
        return availableArea;
    }

    public Booking createBooking(Booking booking) {
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