package ru.tbank.bookit.book_it_backend.service;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.tbank.bookit.book_it_backend.DTO.CreateBookingRequest;
import ru.tbank.bookit.book_it_backend.DTO.UpdateBookingRequest;
import ru.tbank.bookit.book_it_backend.config.BookingConfig;
import ru.tbank.bookit.book_it_backend.model.Booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class BookingMenuService {
    private final BookingService bookingService;

    public BookingMenuService(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public Optional<Booking> findBooking(UUID bookingId) {
        return bookingService.findBooking(bookingId);
    }

    public List<LocalDate> findAvailableDates(Optional<UUID> areaId) {
        return bookingService.findAvailableDates(areaId);
    }

    public Set<Booking> createBooking(CreateBookingRequest request) {
        return bookingService.createBooking(request);
    }

    public List<List<Pair<LocalDateTime, LocalDateTime>>> findAvailableTime(LocalDate date, Optional<UUID> areaId) {
        return bookingService.findAvailableTime(date, areaId);
    }

    public List<Booking> findAll() {
        return bookingService.findAll();
    }
    
    public List<UUID> findAvailableAreas(Set<LocalDateTime> startTimes) {
        return bookingService.findAvailableAreas(startTimes);
    }

    public Set<Pair<LocalDateTime, LocalDateTime>> findClosestAvailableTimes(UUID areaId) {
        return bookingService.findClosestAvailableTimes(areaId);
    }

    public Booking updateBooking(UUID bookingId, UpdateBookingRequest request) {
        return bookingService.updateBooking(
                bookingId,
                request.areaId(),
                request.startTime(),
                request.endTime()
                                           );
    }
}