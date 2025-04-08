package ru.tbank.bookit.book_it_backend.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.bookit.book_it_backend.DTO.CreateBookingRequest;
import ru.tbank.bookit.book_it_backend.config.BookingConfig;
import ru.tbank.bookit.book_it_backend.model.Area;
import ru.tbank.bookit.book_it_backend.model.Booking;
import ru.tbank.bookit.book_it_backend.model.BookingStatus;
import ru.tbank.bookit.book_it_backend.model.User;
import ru.tbank.bookit.book_it_backend.repository.AreaRepository;
import ru.tbank.bookit.book_it_backend.repository.BookingRepository;
import ru.tbank.bookit.book_it_backend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final AreaRepository areaRepository;

    private final BookingConfig bookingConfig;

    @Autowired
    public BookingService(BookingRepository bookings, UserRepository userRepository, AreaRepository areaRepository, BookingConfig bookingConfig) {
        this.bookingRepository = bookings;
        this.userRepository = userRepository;
        this.areaRepository = areaRepository;
        this.bookingConfig = bookingConfig;
    }

    public Booking createBooking(Booking booking) {
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCreatedAt(LocalDateTime.now());
        bookingRepository.save(booking);
        return booking;
    }

    @Transactional
    public Booking createBooking(CreateBookingRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        Area area = areaRepository.findById(request.getAreaId())
                .orElseThrow(() -> new EntityNotFoundException("Area not found with id: " + request.getAreaId()));

        if (!isAreaAvailable(area, request.getStartTime(), request.getEndTime())) {
            throw new IllegalStateException("Area is already booked for this time slot");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setArea(area);
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setQuantity(request.getQuantity());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCreatedAt(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    public void cancelBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELED) {
            throw new IllegalStateException("Booking already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELED);
        bookingRepository.save(booking);
    }

    public List<Booking> getCurrentBookings(String userId) {
        return bookingRepository.findCurrentBookingsByUser(userId, LocalDateTime.now());
    }

    public List<Booking> getFutureBookings(String userId) {
        return bookingRepository.findFutureBookingsByUser(userId, LocalDateTime.now());
    }

    public List<Booking> getPastBookings(String userId) {
        return bookingRepository.findPastBookingsByUser(userId, LocalDateTime.now());
    }

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    // TO DO
    private boolean isAreaAvailable(Area area, LocalDateTime startTime, LocalDateTime endTime) {
        return true;
    }
}