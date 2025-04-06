package ru.tbank.bookit.book_it_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.tbank.bookit.book_it_backend.config.BookingConfig;
import ru.tbank.bookit.book_it_backend.model.Area;
import ru.tbank.bookit.book_it_backend.model.Booking;
import ru.tbank.bookit.book_it_backend.model.BookingStatus;
import ru.tbank.bookit.book_it_backend.repository.AreaRepository;
import ru.tbank.bookit.book_it_backend.repository.BookingRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingMenuService {
    private final BookingRepository bookingRepository;
    private final AreaRepository areaRepository;
    private final BookingConfig bookingConfig;

    @Autowired
    public BookingMenuService(BookingRepository bookings, AreaRepository areaRepository, BookingConfig bookingConfig) {
        this.bookingRepository = bookings;
        this.areaRepository = areaRepository;
        this.bookingConfig = bookingConfig;
    }

    public Booking findBooking(String bookingId) {
        return bookingRepository.findByUserId(bookingId);
    }

    public List<LocalDate> findAvailableDates() {
        List<LocalDate> availableDates = new ArrayList<>();
        List<Booking> bookings = bookingRepository.findAll();
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
        List<Booking> bookings = areaId.isEmpty() ?
                bookingRepository.findByDate(date) :
                bookingRepository.findByDateAndArea(date, areaId.get());

        LocalDateTime start = LocalDateTime.MIN;
        LocalDateTime end = LocalDateTime.MIN;

        List<Pair<LocalDateTime, LocalDateTime>> availableTime = new ArrayList<>();
        for (long i = bookingConfig.getStartWork() + 1; i < bookingConfig.getEndWork(); ++i) {
            LocalDateTime currHour = LocalDateTime.now().toLocalDate().atTime((int)i, 0);
            if (bookings.stream().anyMatch(b -> currHour.equals(b.getEndTime())) &&
                    bookings.stream().noneMatch(b -> currHour.equals(b.getStartTime()))) {
                start = currHour;
            } else if (bookings.stream().anyMatch(b -> currHour.equals(b.getStartTime())) &&
                bookings.stream().noneMatch(b -> currHour.equals(b.getEndTime()))) {
                end = currHour;
                availableTime.addLast(Pair.of(start, end));
            }
        }

        return availableTime;
    }

    public List<String> findAvailableArea(LocalDateTime time) {
        List<String> availableAreas = areaRepository.findAll().stream()
                                                       .map(Area::getId)
                                                       .toList();
        List<Booking> bookings = bookingRepository.findByDatetime(time);

        for (Booking b : bookings) {
            availableAreas.remove(b.getAreaId());
        }

        return availableAreas;
    }

    public Booking createBooking(Booking booking) {
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCreatedAt(LocalDateTime.now());
        bookingRepository.save(booking);
        return booking;
    }

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }
}