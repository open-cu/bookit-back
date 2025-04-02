package ru.tbank.bookit.book_it_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.tbank.bookit.book_it_backend.config.BookingConfig;
import ru.tbank.bookit.book_it_backend.model.Booking;
import ru.tbank.bookit.book_it_backend.model.BookingStatus;
import ru.tbank.bookit.book_it_backend.repository.BookingRepository;

import java.time.Duration;
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
        return bookingRepository.findBookingById(bookingId);
    }

    public List<LocalDate> findAvailableDates() {
        List<LocalDate> availableDates = List.of();
        List<Booking> bookings = bookingRepository.findBookings();

        for (int i = 0; i <= bookingConfig.getMaxDaysForward(); ++i) {
            LocalDate date = LocalDate.now().plusDays(i);
            Duration duration = Duration.ZERO;
            List<Booking> bookingsInThatDay = bookings
                    .stream()
                    .filter(b -> date.equals(b.getStartTime().toLocalDate()))
                    .toList();

            for (Booking booking : bookingsInThatDay) {
                duration = duration.plus(Duration.between(booking.getStartTime(), booking.getEndTime()));
            }

            if (duration.compareTo(Duration.ofHours(bookingConfig.getEndWork() - bookingConfig.getStartWork())) >= 0) {
                availableDates.addLast(date);
            }
        }

        return availableDates;
    }

    public List<Pair<LocalDateTime, LocalDateTime>> findAvailableTime(LocalDate date, Optional<String> areaId) {
        List<Pair<LocalDateTime, LocalDateTime>> availableTime = List.of();
        List<Booking> bookings = List.of();

        if (areaId.isEmpty()) {
            bookings = bookingRepository.findBookingsInDate(date);
        }
        else {
            bookings = bookingRepository.findBookingsInDateAndArea(date, Long.valueOf(areaId.get()));
        }

        LocalDateTime start = LocalDateTime.MIN;
        LocalDateTime end = LocalDateTime.MIN;

        for (long i = bookingConfig.getStartWork() + 1; i < bookingConfig.getEndWork(); ++i)
        {
            LocalDateTime currHour = LocalDateTime.now().toLocalDate().atTime((int)i, 0);

            if (bookings.stream().anyMatch(b -> currHour.equals(b.getEndTime())) &&
                    bookings.stream().noneMatch(b -> currHour.equals(b.getStartTime())))
            {
                start = currHour;
            }
            else if (bookings.stream().anyMatch(b -> currHour.equals(b.getStartTime())) &&
                bookings.stream().noneMatch(b -> currHour.equals(b.getEndTime())))
            {
                end = currHour;
                availableTime.addLast(Pair.of(start, end));
            }
        }

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