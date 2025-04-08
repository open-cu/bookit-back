package ru.tbank.bookit.book_it_backend.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.bookit.book_it_backend.DTO.CreateBookingRequest;
import ru.tbank.bookit.book_it_backend.config.BookingConfig;
import ru.tbank.bookit.book_it_backend.model.*;
import ru.tbank.bookit.book_it_backend.repository.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final HallOccupancyRepository hallOccupancyRepository;
    private final ScheduleRepository scheduleRepository;
    private final AreaRepository areaRepository;
    private final UserRepository userRepository;

    private final BookingConfig bookingConfig;

    @Autowired
    public BookingService(BookingRepository bookings, HallOccupancyRepository hallOccupancyRepository,
                          ScheduleRepository scheduleRepository, BookingConfig bookingConfig,
                          AreaRepository areaRepository, UserRepository userRepository) {
        this.bookingRepository = bookings;
        this.hallOccupancyRepository = hallOccupancyRepository;
        this.scheduleRepository = scheduleRepository;
        this.bookingConfig = bookingConfig;
        this.areaRepository = areaRepository;
        this.userRepository = userRepository;
    }

    public Optional<Booking> findBooking(UUID bookingId) {
        return bookingRepository.findByUserId(bookingId);
    }

    public List<LocalDate> findAvailableDates(Optional<UUID> areaId) {
        if (areaId.isPresent()) {
            Optional<Area> area = areaRepository.findById(areaId.get());
            if(area.isPresent() && area.get().getType().equals(AreaType.WORKPLACE)) {
                return findHallAvailableDates();
            }
            return findAvailableDatesByArea(areaId);
        } else {
            return findHallAvailableDates();
        }
    }

    private List<LocalDate> findAvailableDatesByArea(Optional<UUID> areaId) {
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

    public List<Pair<LocalDateTime, LocalDateTime>> findAvailableTime(LocalDate date, Optional<UUID> areaId) {
        List<Pair<LocalDateTime, LocalDateTime>> availableTime = new ArrayList<>();
        if (!areaId.isEmpty()) {
            List<Booking> bookings = bookingRepository.findByDateAndArea(date, areaId.get());
            addFreeTimes(availableTime, bookings);
        } else {
            List<UUID> availableAreas = areaRepository.findAll().stream()
                    .map(b -> b.getId())
                    .toList();
            for (UUID a : availableAreas) {
                List<Booking> bookings = bookingRepository.findByDateAndArea(date, a);
                addFreeTimes(availableTime, bookings);
            }
        }
        return availableTime;
    }

    public void cancelBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELED) {
            throw new IllegalStateException("Booking already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELED);
        bookingRepository.save(booking);
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

    public List<Booking> getCurrentBookings(UUID userId) {
        return bookingRepository.findCurrentBookingsByUser(userId, LocalDateTime.now());
    }

    public List<Booking> getFutureBookings(UUID userId) {
        return bookingRepository.findFutureBookingsByUser(userId, LocalDateTime.now());
    }

    public List<Booking> getPastBookings(UUID userId) {
        return bookingRepository.findPastBookingsByUser(userId, LocalDateTime.now());
    }

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    public List<Booking> findByStartDatetime(LocalDateTime time) {
        return bookingRepository.findByStartDatetime(time);
    }

    // TO DO
    private boolean isAreaAvailable(Area area, LocalDateTime startTime, LocalDateTime endTime) {
        return true;
    }


    public List<UUID> findAvailableAreas(LocalDateTime time) {
        List<UUID> availableAreas = areaRepository.findAll().stream()
                                                  .map(b -> b.getId())
                                                  .toList();
        List<Booking> bookings = findByStartDatetime(time);

        for (Booking b : bookings) {
            availableAreas.remove(b.getAreaId());
        }

        return availableAreas;
    }
}