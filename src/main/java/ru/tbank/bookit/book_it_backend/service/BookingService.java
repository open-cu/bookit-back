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
import java.util.*;

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
            if (scheduleRepository.findByDate(date).isPresent()) {
                continue;
            }
            Optional<Integer> countReservedPlaces = hallOccupancyRepository.countReservedPlacesByDate(date);
            if (countReservedPlaces.get() < bookingConfig.getHallMaxCapacity() *
                    (bookingConfig.getEndWork() - bookingConfig.getStartWork())) {
                availableDates.add(date);
            }
        }
        for (int i = 5; i < 30; i++) {
            LocalDate date = today.plusDays(i);
            if (scheduleRepository.findByDate(date).isEmpty()) {
                availableDates.add(date);
            }
        }
        return availableDates;
    }

    private void addFreeTimes(List<Pair<LocalDateTime, LocalDateTime>> availableTime, LocalDate date, List<Booking> bookings)
    {
        for (long i = bookingConfig.getStartWork(); i < bookingConfig.getEndWork(); ++i) {
            LocalDateTime currHour = date.atTime((int) i, 0);
            LocalDateTime nextHour = currHour.plusHours(1);
            if (bookings.stream().noneMatch(b -> currHour.isBefore(b.getEndTime()) &&
                    nextHour.isAfter(b.getStartTime()))) {
                Pair<LocalDateTime, LocalDateTime> pair = Pair.of(currHour, nextHour);
                if (!availableTime.contains(pair)) {
                    availableTime.addLast(pair);
                }
            }
        }
    }

    public List<Pair<LocalDateTime, LocalDateTime>> findAvailableTime(LocalDate date, Optional<UUID> areaId) {
        List<Pair<LocalDateTime, LocalDateTime>> availableTime = new ArrayList<>();
        if (areaId.isPresent()) {
            List<Booking> bookings = bookingRepository.findByDateAndArea(date, areaId.get());
            addFreeTimes(availableTime, date, bookings);
        } else {
            List<UUID> availableAreas = areaRepository.findAll().stream()
                    .map(Area::getId)
                    .toList();
            for (UUID a : availableAreas) {
                List<Booking> bookings = bookingRepository.findByDateAndArea(date, a);
                addFreeTimes(availableTime, date, bookings);
            }
        }
        availableTime.sort(Comparator.comparing(Pair::getFirst));
        return availableTime;
    }

    public void cancelBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Booking not found with id: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELED) {
            throw new IllegalStateException("Booking already cancelled");
        }

        if (booking.getStartTime().isBefore(LocalDateTime.now())) {
            booking.setStatus(BookingStatus.COMPLETED);
        }
        else {
            booking.setStatus(BookingStatus.CANCELED);
        }
        bookingRepository.save(booking);
    }

    @Transactional
    public Booking createBooking(CreateBookingRequest request) {
        User user = userRepository.findById(request.getUserId())
                                  .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        Area area = areaRepository.findById(request.getAreaId())
                                  .orElseThrow(() -> new EntityNotFoundException("Area not found with id: " + request.getAreaId()));

        if (request.getTimePeriods().isEmpty()) {
            throw new IllegalStateException("There are no chosen time periods");
        }

        for (Pair<LocalDateTime, LocalDateTime> t : request.getTimePeriods()) {
            if (!isAreaAvailable(area, t.getFirst(), t.getSecond())) {
                throw new IllegalStateException("Area is already booked for this time slot");
            }
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setArea(area);

        Optional<Pair<LocalDateTime, LocalDateTime>> startTime = request.getTimePeriods().stream().min((a, b) -> a.getFirst().compareTo(b.getFirst()));
        Optional<Pair<LocalDateTime, LocalDateTime>> endTime = request.getTimePeriods().stream().max((a, b) -> a.getSecond().compareTo(b.getSecond()));
        booking.setStartTime(startTime.get().getFirst());
        booking.setEndTime(endTime.get().getSecond());

        booking.setQuantity(request.getQuantity());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCreatedAt(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    public List<Booking> getCurrentBookings(UUID userId) {
        return getBookingsByTimeTag(userId, TimeTag.CURRENT);
    }

    public List<Booking> getFutureBookings(UUID userId) {
        return getBookingsByTimeTag(userId, TimeTag.FUTURE);
    }

    public List<Booking> getPastBookings(UUID userId) {
        return getBookingsByTimeTag(userId, TimeTag.PAST);
    }

    public List<Booking> getBookingsByTimeTag(UUID userId, TimeTag timeTag) {
        final LocalDateTime now = LocalDateTime.now();
        List<Booking> bookingList = switch (timeTag) {
            case CURRENT -> bookingRepository.findCurrentBookingsByUser(userId, now)
                                             .stream()
                                             .filter(booking -> booking.getStatus() != BookingStatus.COMPLETED)
                                             .toList();
            case FUTURE -> bookingRepository.findFutureBookingsByUser(userId, now);
            case PAST -> {
                List<Booking> pastBookings = bookingRepository.findPastBookingsByUser(userId, now);
                List<Booking> currentCompletedBookings =
                        bookingRepository.findCurrentBookingsByUser(userId, now)
                                         .stream()
                                         .filter(booking -> booking.getStatus() == BookingStatus.COMPLETED)
                                         .toList();
                pastBookings.addAll(currentCompletedBookings);
                yield pastBookings;
            }
        };
        return bookingList.stream().filter(booking -> booking.getStatus() != BookingStatus.CANCELED).toList();
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