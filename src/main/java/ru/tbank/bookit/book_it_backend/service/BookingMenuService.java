package ru.tbank.bookit.book_it_backend.service;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.tbank.bookit.book_it_backend.config.BookingConfig;
import ru.tbank.bookit.book_it_backend.model.Booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingMenuService {
    private final BookingConfig bookingConfig;
    private final BookingService bookingService;
    private final AreaService areaService;

    public BookingMenuService(BookingService bookingService, BookingConfig bookingConfig, AreaService areaService) {
        this.bookingConfig = bookingConfig;
        this.bookingService = bookingService;
        this.areaService = areaService;
    }

    public Optional<Booking> findBooking(long bookingId) {
        return bookingService.findBooking(bookingId);
    }

    public List<LocalDate> findAvailableDates(Optional<String> areaId) {
        return bookingService.findAvailableDates(areaId);
    }

    public Booking createBooking(Booking booking) {
        return bookingService.createBooking(booking);
    }

    public List<Pair<LocalDateTime, LocalDateTime>> findAvailableTime(LocalDate date, Optional<String> areaId) {
        return bookingService.findAvailableTime(date, areaId);
    }

    public List<Booking> findAll() {
        return bookingService.findAll();
    }
    
    public List<String> findAvailableAreas(LocalDateTime time) {
        return areaService.findAvailableAreas(time);
    }
}