package ru.tbank.bookit.book_it_backend.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.bookit.book_it_backend.DTO.CreateBookingRequest;
import ru.tbank.bookit.book_it_backend.config.BookingConfig;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final HallOccupancyRepository hallOccupancyRepository;
    private final ScheduleRepository scheduleRepository;
    private final AreaRepository areaRepository;
    private final UserRepository userRepository;

    private final BookingConfig bookingConfig;

    @Autowired
    public BookingService(UserService userService, BookingRepository bookings, HallOccupancyRepository hallOccupancyRepository,
                          ScheduleRepository scheduleRepository, BookingConfig bookingConfig,
                          AreaRepository areaRepository, UserRepository userRepository) {
        this.userService = userService;
        this.bookingRepository = bookings;
        this.hallOccupancyRepository = hallOccupancyRepository;
        this.scheduleRepository = scheduleRepository;
        this.bookingConfig = bookingConfig;
        this.areaRepository = areaRepository;
        this.userRepository = userRepository;
    }

    public Optional<Booking> findBooking(UUID bookingId) {
        return bookingRepository.findById(bookingId);
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

    private boolean isAreaAvailable(LocalDateTime currHour, List<Booking> bookings, Area area) {
        if (area.getType() != AreaType.WORKPLACE) {
            return bookings.stream().noneMatch(b -> bookingIncludeHour(currHour, b));
        } else {
            Optional<HallOccupancy> hallOccupancy = hallOccupancyRepository.findById(currHour);
            if (hallOccupancy.isPresent() &&
                    bookings.stream().noneMatch(b -> b.getUserId().equals(userService.getCurrentUser().getId()) &&
                            bookingIncludeHour(currHour, b))) {
                return hallOccupancy.get().getReservedPlaces() < bookingConfig.getHallMaxCapacity();
            }
        }
        return false;
    }

    private boolean bookingIncludeHour(LocalDateTime currHour, Booking b) {
        return currHour.isBefore(b.getEndTime()) && currHour.plusHours(1).isAfter(b.getStartTime());
    }

    private void addFreeTimes(List<List<Pair<LocalDateTime, LocalDateTime>>> availableTime, LocalDate date, List<Booking> bookings, Area area)
    {
        for (long i = bookingConfig.getStartWork(); i < bookingConfig.getEndWork(); ++i) {
            LocalDateTime currHour = date.atTime((int)i, 0);
            LocalDateTime nextHour = currHour.plusHours(1);
            if (isAreaAvailable(currHour, bookings, area)) {
                Pair<LocalDateTime, LocalDateTime> pair = Pair.of(currHour, nextHour);
                List<Pair<LocalDateTime, LocalDateTime>> addList = pair.getFirst().getHour() < 12 ? availableTime.get(0) : pair.getFirst().getHour() < 18 ? availableTime.get(1) : availableTime.get(2);
                LocalDateTime now = LocalDateTime.now();
                if (!addList.contains(pair) && (now.getHour() <= pair.getFirst().getHour() || now.getDayOfYear() < pair.getFirst().getDayOfYear())) {
                    addList.addLast(pair);
                }
            }
        }
    }

    public List<List<Pair<LocalDateTime, LocalDateTime>>> findAvailableTime(LocalDate date, Optional<UUID> areaId, Optional<UUID> bookingId) {
        List<List<Pair<LocalDateTime, LocalDateTime>>> availableTime = new ArrayList<>();
        availableTime.addLast(new ArrayList<>());
        availableTime.addLast(new ArrayList<>());
        availableTime.addLast(new ArrayList<>());

        if (date.isBefore(LocalDate.now())) {
            return availableTime;
        }

        if (areaId.isPresent()) {
            List<Booking> bookings = bookingRepository.findByDateAndArea(date, areaId.get());
            Optional<Area> checkArea = areaRepository.findById(areaId.get());

            if (checkArea.isEmpty()) {
                throw new RuntimeException("Area with this UUID doesn't exist!");
            }

            addFreeTimes(availableTime, date, bookings, checkArea.get());
        } else {
            List<UUID> availableAreas = areaRepository.findAll().stream()
                    .map(Area::getId)
                    .toList();
            for (UUID a : availableAreas) {
                List<Booking> bookings = bookingRepository.findByDateAndArea(date, a);
                addFreeTimes(availableTime, date, bookings, areaRepository.findById(a).get());
            }
        }

        if (bookingId.isPresent()) {
            Booking booking = bookingRepository.findById(bookingId.get())
                                               .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + bookingId.get()));
            if (booking.getStartTime().toLocalDate().equals(date) && (booking.getAreaId().equals(areaId.orElse(null)) || areaId.isEmpty())) {
                LocalDateTime start = booking.getStartTime();
                LocalDateTime end = booking.getEndTime();

                LocalDateTime current = start;
                while (current.isBefore(end)) {
                    LocalDateTime nextHour = current.plusHours(1);
                    if (nextHour.isAfter(end)) {
                        nextHour = end;
                    }

                    Pair<LocalDateTime, LocalDateTime> pair = Pair.of(current, nextHour);
                    List<Pair<LocalDateTime, LocalDateTime>> addList = current.getHour() < 12 ?
                            availableTime.get(0) : current.getHour() < 18 ?
                            availableTime.get(1) : availableTime.get(2);

                    if (!addList.contains(pair)) {
                        addList.addLast(pair);
                    }

                    current = nextHour;
                }
            }
        }

        for (List<Pair<LocalDateTime, LocalDateTime>> l : availableTime) {
            l.sort(Comparator.comparing(Pair::getFirst));
        }

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
    public Set<Booking> createBooking(CreateBookingRequest request) {
        User user = userRepository.findById(request.userId())
                                  .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.userId()));

        Area area = areaRepository.findById(request.areaId())
                                  .orElseThrow(() -> new EntityNotFoundException("Area not found with id: " + request.areaId()));

        if (request.timePeriods().isEmpty()) {
            throw new IllegalStateException("There are no chosen time periods");
        }

        for (Pair<LocalDateTime, LocalDateTime> t : request.timePeriods()) {
            if (!isAreaAvailable(area, t.getFirst(), t.getSecond())) {
                throw new IllegalStateException("Area is already booked for this time slot");
            }
        }

        ArrayList<Pair<LocalDateTime, LocalDateTime>> times = new ArrayList<>(request.timePeriods());

        times.sort(Comparator.comparing(Pair::getFirst));

        List<Pair<LocalDateTime, LocalDateTime>> merged = new ArrayList<>();
        for (Pair<LocalDateTime, LocalDateTime> curr : times) {
            if (merged.isEmpty()) {
                merged.add(curr);
            } else {
                Pair<LocalDateTime, LocalDateTime> last = merged.get(merged.size() - 1);
                if (last.getSecond().isEqual(curr.getFirst())) {
                    merged.set(merged.size() - 1, Pair.of(last.getFirst(), curr.getSecond()));
                } else {
                    merged.add(curr);
                }
            }
        }

        Set<Booking> result = new HashSet<>();

        for (Pair<LocalDateTime, LocalDateTime> time : merged) {
            Booking booking = new Booking();
            booking.setUser(user);
            booking.setArea(area);

            booking.setStartTime(time.getFirst());
            booking.setEndTime(time.getSecond());

            booking.setQuantity(request.quantity());
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setCreatedAt(LocalDateTime.now());

            result.add(booking);
        }

        if (area.getType().equals(AreaType.WORKPLACE)) {
            for (Pair<LocalDateTime, LocalDateTime> t : request.timePeriods()) {
                for (LocalDateTime time = t.getFirst(); time.isBefore(t.getSecond()); time = time.plusHours(1)) {
                    HallOccupancy hallOccupancy = hallOccupancyRepository.getById(time);
                    hallOccupancy.setReservedPlaces(hallOccupancy.getReservedPlaces() + 1);
                    hallOccupancyRepository.save(hallOccupancy);
                }
            }
        }
        bookingRepository.saveAll(result);

        return result;
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

    @Transactional
    public Booking updateBooking(UUID bookingId, UUID areaId, LocalDateTime startTime, LocalDateTime endTime) {
        Booking booking = bookingRepository.findById(bookingId)
                                           .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELED || booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Unable to update " + booking.getStatus() + " booking");
        }
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }

        boolean sameArea = booking.getAreaId().equals(areaId);
        boolean sameTime = booking.getStartTime().equals(startTime) && booking.getEndTime().equals(endTime);

        if (sameArea && sameTime) {
            return booking;
        }

        Area area = !sameArea
                ? areaRepository.findById(areaId)
                                .orElseThrow(() -> new EntityNotFoundException("Area not found id: " + areaId))
                : booking.getArea();

        if (!sameArea) {
            booking.setArea(area);
        }
        if (!sameTime) {
            booking.setStartTime(startTime);
            booking.setEndTime(endTime);
        }

        return bookingRepository.save(booking);
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


    public List<UUID> findAvailableAreas(Set<LocalDateTime> startTimes) {
        ArrayList<UUID> availableAreas = areaRepository.findAll().stream().filter(area -> area.getType() != AreaType.WORKPLACE)
                                                  .map(Area::getId)
                                                  .collect(Collectors.toCollection(ArrayList::new));
        Area workplace = areaRepository.findByType(AreaType.WORKPLACE).getFirst();
        boolean isWorkplaceAvailable = true;

        for (LocalDateTime time : startTimes) {
            List<Booking> bookings = bookingRepository.findByDatetime(time);
            Set<UUID> bookedAreaIds = bookings.stream()
                                              .map(Booking::getAreaId)
                                              .collect(Collectors.toSet());
            availableAreas = availableAreas.stream()
                                           .filter(id -> !bookedAreaIds.contains(id))
                                           .collect(Collectors.toCollection(ArrayList::new));
            int reservedPlaces = hallOccupancyRepository.getById(time).getReservedPlaces();
            if (reservedPlaces >= bookingConfig.getHallMaxCapacity() ||
                    bookings.stream().anyMatch(b -> b.getUserId().equals(userService.getCurrentUser().getId()) &&
                    bookingIncludeHour(time, b))) {
                isWorkplaceAvailable = false;
            }
        }
        if (isWorkplaceAvailable) {
            availableAreas.add(workplace.getId());
        }

        return availableAreas;
    }

    public Set<Pair<LocalDateTime, LocalDateTime>> findClosestAvailableTimes(UUID areaId) {
        LocalDate currDate = LocalDate.now();
        List<List<Pair<LocalDateTime, LocalDateTime>>> times = List.of();
        while (times.isEmpty()) {
            times = findAvailableTime(currDate, Optional.ofNullable(areaId), Optional.empty());
            currDate.plusDays(1);
        }
        Set<Pair<LocalDateTime, LocalDateTime>> result = new HashSet<>();
        for (List<Pair<LocalDateTime, LocalDateTime>> l : times) {
            for (Pair<LocalDateTime, LocalDateTime> p : l) {
                if (result.size() >= 4) {
                    break;
                }
                result.add(p);
            }
        }
        return result;
    }
}