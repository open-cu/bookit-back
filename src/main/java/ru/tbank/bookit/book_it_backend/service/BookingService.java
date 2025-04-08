package ru.tbank.bookit.book_it_backend.service;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.tbank.bookit.book_it_backend.config.BookingConfig;
import ru.tbank.bookit.book_it_backend.model.Area;
import ru.tbank.bookit.book_it_backend.model.AreaType;
import ru.tbank.bookit.book_it_backend.model.Booking;
import ru.tbank.bookit.book_it_backend.model.BookingStatus;
import ru.tbank.bookit.book_it_backend.repository.AreaRepository;
import ru.tbank.bookit.book_it_backend.repository.BookingRepository;
import ru.tbank.bookit.book_it_backend.repository.HallOccupancyRepository;
import ru.tbank.bookit.book_it_backend.repository.ScheduleRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final HallOccupancyRepository hallOccupancyRepository;
    private final ScheduleRepository scheduleRepository;
    private final AreaRepository areaRepository;

    private final BookingConfig bookingConfig;

    public BookingService(BookingRepository bookings, HallOccupancyRepository hallOccupancyRepository,
                          ScheduleRepository scheduleRepository, BookingConfig bookingConfig,
                          AreaRepository areaRepository) {
        this.bookingRepository = bookings;
        this.hallOccupancyRepository = hallOccupancyRepository;
        this.scheduleRepository = scheduleRepository;
        this.bookingConfig = bookingConfig;
        this.areaRepository = areaRepository;
    }

    public Booking createBooking(Booking booking) {
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCreatedAt(LocalDateTime.now());
        bookingRepository.save(booking);
        return booking;
    }

    public Optional<Booking> findBooking(long bookingId) {
        return bookingRepository.findByUserId(bookingId);
    }

    public List<LocalDate> findAvailableDates(Optional<String> areaId) {
        if (areaId.isPresent()) {
            Optional<Area> area = areaRepository.findById(Long.valueOf(areaId.get()));
            if(area.isPresent() && area.get().getType().equals(AreaType.WORKPLACE)) {
                return findHallAvailableDates();
            }
            return findAvailableDatesByArea(areaId);
        } else {
            return findHallAvailableDates();
        }
    }

    private List<LocalDate> findAvailableDatesByArea(Optional<String> areaId) {
        List<LocalDate> availableDates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        List<Booking> relevantBookings = bookingRepository.findByAreaId(areaId.get()); //todo проверить что area существует

        for (int i = 0; i <= bookingConfig.getMaxDaysForward(); ++i) {
            LocalDate date = today.plusDays(i);

            if (scheduleRepository.findByDate(date).isPresent()) {
                continue;
            }

            Duration bookedDuration = relevantBookings.stream()
                    .filter(b -> date.equals(b.getStartTime().toLocalDate()))
                    .map(b -> Duration.between(b.getStartTime(), b.getEndTime()))
                    .reduce(Duration.ZERO, Duration::plus);

            Duration totalWorkDuration = Duration.ofHours(
                    bookingConfig.getEndWork() - bookingConfig.getStartWork());

            if (bookedDuration.compareTo(totalWorkDuration) < 0) {
                availableDates.add(date);
            }
        }
        return availableDates;
    }

    private List<LocalDate> findHallAvailableDates() {
        List<LocalDate> availableDates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i <= 4; ++i) {
            LocalDate date = today.plusDays(i);
            scheduleRepository.findAll();
            if (scheduleRepository.findByDate(date).isPresent()) {
                continue;
            }

            Optional<Integer> countReservedPlaces = hallOccupancyRepository.countReservedPlacesByDate(date);
            if (countReservedPlaces.get() < bookingConfig.getHallMaxCapacity() * (bookingConfig.getEndWork() - bookingConfig.getStartWork())) {
                availableDates.add(date);
            }
        }
        return availableDates;
    }

    private void addFreeTimes(List<Pair<LocalDateTime, LocalDateTime>> availableTime, List<Booking> bookings)
    {
        for (long i = bookingConfig.getStartWork(); i <= bookingConfig.getEndWork(); ++i) {
            LocalDateTime currHour = LocalDateTime.now().toLocalDate().atTime((int) i, 0);
            if (bookings.stream().noneMatch(b -> currHour.compareTo(b.getStartTime()) >= 0 &&
                    currHour.compareTo(b.getEndTime()) < 0))
            {
                Pair<LocalDateTime, LocalDateTime> pair = Pair.of(currHour, currHour.plusHours(1));
                if (!availableTime.contains(pair)) {
                    availableTime.addLast(pair);
                }
            }
        }
    }

    public List<Pair<LocalDateTime, LocalDateTime>> findAvailableTime(LocalDate date, Optional<String> areaId) {
        List<Pair<LocalDateTime, LocalDateTime>> availableTime = new ArrayList<>();
        if (!areaId.isEmpty()) {
            List<Booking> bookings = bookingRepository.findByDateAndArea(date, Long.valueOf(areaId.get()));
            addFreeTimes(availableTime, bookings);
        } else {
            List<String> availableAreas = areaRepository.findAll().stream()
                    .map(b -> Long.toString(b.getId()))
                    .toList();
            for (String a : availableAreas) {
                List<Booking> bookings = bookingRepository.findByDateAndArea(date, Long.valueOf(a));
                addFreeTimes(availableTime, bookings);
            }
        }
        return availableTime;
    }

    public void cancelBooking(long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELED) {
            throw new IllegalStateException("Booking already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELED);
        bookingRepository.save(booking);
    }

    public List<Booking> getCurrentBookings(Long userId) {
        return bookingRepository.findCurrentBookingsByUser(userId, LocalDateTime.now());
    }

    public List<Booking> getFutureBookings(Long userId) {
        return bookingRepository.findFutureBookingsByUser(userId, LocalDateTime.now());
    }

    public List<Booking> getPastBookings(Long userId) {
        return bookingRepository.findPastBookingsByUser(userId, LocalDateTime.now());
    }

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    public List<Booking> findByStartDatetime(LocalDateTime time) {
        return bookingRepository.findByStartDatetime(time);
    }
}