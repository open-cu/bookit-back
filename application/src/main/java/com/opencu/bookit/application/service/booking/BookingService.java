package com.opencu.bookit.application.service.booking;

import com.opencu.bookit.application.config.BookingConfig;
import com.opencu.bookit.application.port.out.area.LoadAreaPort;
import com.opencu.bookit.application.port.in.booking.CRUDBookingUseCase;
import com.opencu.bookit.application.port.out.booking.DeleteBookingPort;
import com.opencu.bookit.application.port.out.booking.LoadBookingPort;
import com.opencu.bookit.application.port.out.booking.SaveBookingPort;
import com.opencu.bookit.application.port.out.schedule.LoadSchedulePort;
import com.opencu.bookit.application.port.out.user.LoadAuthorizationInfoPort;
import com.opencu.bookit.application.port.out.user.LoadUserPort;
import com.opencu.bookit.application.service.user.UserService;
import com.opencu.bookit.application.port.out.statstics.LoadHallOccupancyPort;
import com.opencu.bookit.application.port.out.statstics.SaveHallOccupancyPort;
import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.area.AreaType;
import com.opencu.bookit.domain.model.booking.BookingModel;
import com.opencu.bookit.domain.model.booking.BookingStatus;
import com.opencu.bookit.domain.model.booking.TimeTag;
import com.opencu.bookit.domain.model.statistics.HallOccupancyModel;
import com.opencu.bookit.domain.model.user.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private final UserService userService;
    private final LoadBookingPort loadBookingPort;
    private final SaveBookingPort saveBookingPort;
    private final DeleteBookingPort deleteBookingPort;
    private final LoadHallOccupancyPort loadHallOccupancyPort;
    private final SaveHallOccupancyPort saveHallOccupancyPort;
    private final LoadSchedulePort loadSchedulePort;
    private final LoadAreaPort loadAreaPort;
    private final LoadUserPort loadUserPort;
    private final LoadAuthorizationInfoPort loadAuthorizationInfoPort;

    private final BookingConfig bookingConfig;

    @Autowired
    public BookingService(UserService userService, LoadBookingPort loadBookingPort, SaveBookingPort saveBookingPort, DeleteBookingPort deleteBookingPort,
                          LoadHallOccupancyPort loadHallOccupancyPort, SaveHallOccupancyPort saveHallOccupancyPort,
                          LoadSchedulePort loadSchedulePort, BookingConfig bookingConfig,
                          LoadAreaPort loadAreaPort, LoadUserPort loadUserPort,
                          LoadAuthorizationInfoPort loadAuthorizationInfoPort) {
        this.userService = userService;
        this.loadBookingPort = loadBookingPort;
        this.saveBookingPort = saveBookingPort;
        this.deleteBookingPort = deleteBookingPort;
        this.loadHallOccupancyPort = loadHallOccupancyPort;
        this.saveHallOccupancyPort = saveHallOccupancyPort;
        this.loadSchedulePort = loadSchedulePort;
        this.bookingConfig = bookingConfig;
        this.loadAreaPort = loadAreaPort;
        this.loadUserPort = loadUserPort;
        this.loadAuthorizationInfoPort = loadAuthorizationInfoPort;
    }

    public Optional<BookingModel> findBooking(UUID bookingId) {
        return loadBookingPort.findById(bookingId);
    }

    public List<LocalDate> findAvailableDates(Optional<UUID> areaId) {
        if (areaId.isPresent()) {
            Optional<AreaModel> area = loadAreaPort.findById(areaId.get());
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
        LocalDate today = LocalDate.now(bookingConfig.getZoneId());
        List<BookingModel> relevantBookingModels = loadBookingPort.findByAreaId(areaId.orElseThrow());

        for (int i = 0; i <= bookingConfig.getMaxDaysForward(); ++i) {
            LocalDate date = today.plusDays(i);

            if (loadSchedulePort.findByDate(date).isPresent()) {
                continue;
            }

            Duration bookedDuration = relevantBookingModels.stream()
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
        LocalDate today = LocalDate.now(bookingConfig.getZoneId());
        for (int i = 0; i <= 4; ++i) {
            LocalDate date = today.plusDays(i);
            if (loadSchedulePort.findByDate(date).isPresent()) {
                continue;
            }
            Optional<Integer> countReservedPlaces = loadHallOccupancyPort.countReservedPlacesByDate(date);
            if (countReservedPlaces.get() < bookingConfig.getHallMaxCapacity() *
                    (bookingConfig.getEndWork() - bookingConfig.getStartWork())) {
                availableDates.add(date);
            }
        }
        for (int i = 5; i < 30; i++) {
            LocalDate date = today.plusDays(i);
            if (loadSchedulePort.findByDate(date).isEmpty()) {
                availableDates.add(date);
            }
        }
        return availableDates;
    }

    private boolean isAreaAvailable(LocalDateTime currHour, List<BookingModel> bookingModels, AreaModel areaModel) {
        if (areaModel.getType() != AreaType.WORKPLACE) {
            UUID areaId = areaModel.getId();
            return bookingModels.stream().noneMatch(b -> bookingIncludeHour(currHour, b) &&
                    b.getAreaId().equals(areaId));
        } else {
            Optional<HallOccupancyModel> hallOccupancy = loadHallOccupancyPort.findById(currHour);
            UUID userId = loadAuthorizationInfoPort.getCurrentUser().getId();
            if (hallOccupancy.isPresent() && bookingModels.stream().noneMatch(b -> b.getUserId().equals(userId)
                    && bookingIncludeHour(currHour, b))) {
                return hallOccupancy.get().getReservedPlaces() < bookingConfig.getHallMaxCapacity();
            }
        }
        return false;
    }

    private boolean bookingIncludeHour(LocalDateTime currHour, BookingModel b) {
        return currHour.isBefore(b.getEndTime()) && currHour.plusHours(1).isAfter(b.getStartTime());
    }

    private void addFreeTimes(List<List<Pair<LocalDateTime, LocalDateTime>>> availableTime, LocalDate date, List<BookingModel> bookingModels, AreaModel areaModel)
    {
        for (long i = bookingConfig.getStartWork(); i < bookingConfig.getEndWork(); ++i) {
            LocalDateTime currHour = date.atTime((int)i, 0);
            LocalDateTime nextHour = currHour.plusHours(1);
            if (isAreaAvailable(currHour, bookingModels, areaModel)) {
                Pair<LocalDateTime, LocalDateTime> pair = Pair.of(currHour, nextHour);
                List<Pair<LocalDateTime, LocalDateTime>> addList = pair.getFirst().getHour() < 12 ? availableTime.get(0) : pair.getFirst().getHour() < 18 ? availableTime.get(1) : availableTime.get(2);
                LocalDateTime now = LocalDateTime.now(bookingConfig.getZoneId());
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

        if (date.isBefore(LocalDate.now(bookingConfig.getZoneId()))) {
            return availableTime;
        }

        if (areaId.isPresent()) {
            List<BookingModel> bookingModels = loadBookingPort.findByDateAndArea(date, areaId.get());
            Optional<AreaModel> checkArea = loadAreaPort.findById(areaId.get());

            if (checkArea.isEmpty()) {
                throw new NoSuchElementException("Area with this UUID " + areaId.get() + " doesn't exist!");
            }

            addFreeTimes(availableTime, date, bookingModels, checkArea.get());
        } else {
            List<UUID> availableAreas = loadAreaPort.findAll().stream()
                                                    .map(AreaModel::getId)
                                                    .toList();
            for (UUID a : availableAreas) {
                List<BookingModel> bookingModels = loadBookingPort.findByDateAndArea(date, a);
                addFreeTimes(availableTime, date, bookingModels, loadAreaPort.findById(a).get());
            }
        }

        if (bookingId.isPresent()) {
            BookingModel bookingModel = loadBookingPort.findById(bookingId.get())
                                                       .orElseThrow(() -> new NoSuchElementException("Booking not found with id: " + bookingId.get()));
            if (bookingModel.getStartTime().toLocalDate().equals(date) && (bookingModel.getAreaId().equals(areaId.orElse(null)) || areaId.isEmpty())) {
                LocalDateTime start = bookingModel.getStartTime();
                LocalDateTime end = bookingModel.getEndTime();

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
        BookingModel bookingModel = loadBookingPort.findById(bookingId).orElseThrow(() -> new NoSuchElementException("Booking not found with id: " + bookingId));

        if (bookingModel.getStatus() == BookingStatus.CANCELED) {
            throw new IllegalStateException("Booking already cancelled");
        }

        if (bookingModel.getStartTime().isBefore(LocalDateTime.now(bookingConfig.getZoneId()))) {
            bookingModel.setStatus(BookingStatus.COMPLETED);
        }
        else {
            bookingModel.setStatus(BookingStatus.CANCELED);
            if (bookingModel.getAreaModel().getType().equals(AreaType.WORKPLACE)) {
                for (LocalDateTime time = bookingModel.getStartTime(); time.isBefore(bookingModel.getEndTime()); time = time.plusHours(1)) {
                    HallOccupancyModel hallOccupancyModel = loadHallOccupancyPort.getByDateTime(time).orElseThrow();
                    hallOccupancyModel.setReservedPlaces(hallOccupancyModel.getReservedPlaces() - 1);
                    saveHallOccupancyPort.save(hallOccupancyModel);
                }
            }
        }
        saveBookingPort.save(bookingModel);
    }

    @Transactional
    public List<BookingModel> createBooking(CRUDBookingUseCase.CreateBookingCommand createBookingCommand) {
        UserModel userModel = loadUserPort.findById(createBookingCommand.userId())
                                          .orElseThrow(() -> new NoSuchElementException("User not found with id: " + createBookingCommand.userId()));

        AreaModel areaModel = loadAreaPort.findById(createBookingCommand.areaId())
                                          .orElseThrow(() -> new NoSuchElementException("Area not found with id: " + createBookingCommand.areaId()));

        if (createBookingCommand.timePeriods().isEmpty()) {
            throw new IllegalArgumentException("There are no chosen time periods");
        }

        for (Pair<LocalDateTime, LocalDateTime> time : createBookingCommand.timePeriods()) {
            Set<LocalDateTime> eachTime = new HashSet<>();
            for (LocalDateTime t = time.getFirst(); t.isBefore(time.getSecond()); t = t.plusHours(1)) {
                if (!isOnlyOneBooking(t)) {
                    throw new IllegalArgumentException("You already have a booking at " + t);
                }
                eachTime.add(t);
            }
            if (!findAvailableAreas(eachTime).contains(areaModel.getId())) {
                throw new IllegalArgumentException("Area is not available for requested time periods");
            }
        }

        ArrayList<Pair<LocalDateTime, LocalDateTime>> times = new ArrayList<>(createBookingCommand.timePeriods());

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

        Set<BookingModel> result = new HashSet<>();

        for (Pair<LocalDateTime, LocalDateTime> time : merged) {
            BookingModel bookingModel = new BookingModel();
            bookingModel.setUserModel(userModel);
            bookingModel.setAreaModel(areaModel);

            bookingModel.setStartTime(time.getFirst());
            bookingModel.setEndTime(time.getSecond());

            bookingModel.setQuantity(createBookingCommand.quantity());
            bookingModel.setStatus(BookingStatus.CONFIRMED);
            bookingModel.setCreatedAt(LocalDateTime.now(bookingConfig.getZoneId()));

            result.add(bookingModel);
        }

        if (areaModel.getType().equals(AreaType.WORKPLACE)) {
            for (Pair<LocalDateTime, LocalDateTime> t : createBookingCommand.timePeriods()) {
                for (LocalDateTime time = t.getFirst(); time.isBefore(t.getSecond()); time = time.plusHours(1)) {
                    HallOccupancyModel hallOccupancyModel = loadHallOccupancyPort.getByDateTime(time).orElseThrow();
                    hallOccupancyModel.setReservedPlaces(hallOccupancyModel.getReservedPlaces() + 1);
                    saveHallOccupancyPort.save(hallOccupancyModel);
                }
            }
        }

        return saveBookingPort.saveAll(result);
    }

    public List<BookingModel> getCurrentBookings(UUID userId) {
        return getBookingsByTimeTag(userId, TimeTag.CURRENT);
    }

    public List<BookingModel> getFutureBookings(UUID userId) {
        return getBookingsByTimeTag(userId, TimeTag.FUTURE);
    }

    public List<BookingModel> getPastBookings(UUID userId) {
        return getBookingsByTimeTag(userId, TimeTag.PAST);
    }

    public List<BookingModel> getBookingsByTimeTag(UUID userId, TimeTag timeTag) {
        List<BookingModel> bookingModelList = switch (timeTag) {
            case CURRENT -> loadBookingPort.loadBookingsByUser(userId, timeTag)
                                           .stream()
                                           .filter(booking -> booking.getStatus() != BookingStatus.COMPLETED)
                                           .toList();
            case FUTURE -> loadBookingPort.loadBookingsByUser(userId, timeTag);
            case PAST -> {
                List<BookingModel> pastBookingModels = loadBookingPort.loadBookingsByUser(userId, timeTag);
                List<BookingModel> currentCompletedBookingModels =
                        loadBookingPort.loadBookingsByUser(userId, TimeTag.CURRENT)
                                       .stream()
                                       .filter(booking -> booking.getStatus() == BookingStatus.COMPLETED)
                                       .toList();
                pastBookingModels.addAll(currentCompletedBookingModels);
                yield pastBookingModels;
            }
        };
        return bookingModelList.stream().filter(booking -> booking.getStatus() != BookingStatus.CANCELED).toList();
    }

    @Transactional
    public BookingModel updateBooking(UUID bookingId, CRUDBookingUseCase.UpdateBookingQuery request) {
        UUID areaId = request.areaId();
        LocalDateTime startTime = request.startTime();
        LocalDateTime endTime = request.endTime();
        BookingModel bookingModel = loadBookingPort.findById(bookingId)
                                                   .orElseThrow(() -> new NoSuchElementException("Booking not found with id: " + bookingId));

        if (bookingModel.getStatus() == BookingStatus.CANCELED || bookingModel.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Unable to update " + bookingModel.getStatus() + " booking");
        }
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }

        boolean sameArea = bookingModel.getAreaId().equals(areaId);
        boolean sameTime = bookingModel.getStartTime().equals(startTime) && bookingModel.getEndTime().equals(endTime);

        if (sameArea && sameTime) {
            return bookingModel;
        }

        AreaModel areaModel = !sameArea
                ? loadAreaPort.findById(areaId)
                              .orElseThrow(() -> new NoSuchElementException("Area not found id: " + areaId))
                : bookingModel.getAreaModel();

        if (!sameArea) {
            bookingModel.setAreaModel(areaModel);
        }
        if (!sameTime) {
            bookingModel.setStartTime(startTime);
            bookingModel.setEndTime(endTime);
        }

        return saveBookingPort.save(bookingModel);
    }

    public List<BookingModel> findAll() {
        return loadBookingPort.findAll();
    }

    public List<BookingModel> findByStartDatetime(LocalDateTime time) {
        return loadBookingPort.findByStartDatetime(time);
    }


    public List<UUID> findAvailableAreas(Set<LocalDateTime> requestedTimes) {
        HashSet<UUID> availableAreas = loadAreaPort.findAll().stream().map(AreaModel::getId)
                .collect(Collectors.toCollection(HashSet::new));
        for (LocalDateTime time : requestedTimes) {
            List<BookingModel> bookingModels = loadBookingPort.findAllIncludingTime(time);
            Set<UUID> bookedAreaIds = bookingModels.stream().filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                    .map(BookingModel::getAreaId).collect(Collectors.toSet());
            availableAreas.removeAll(bookedAreaIds);
        }

        UUID workplaceId = loadAreaPort.findByType(AreaType.WORKPLACE).getFirst().getId();
        availableAreas.remove(workplaceId);
        if (requestedTimes.stream().allMatch(this::isWorkplaceAvailable)) {
            availableAreas.add(workplaceId);
        }

        return availableAreas.stream().toList();
    }

    private boolean isWorkplaceAvailable(LocalDateTime time) {

        if (loadSchedulePort.findByDate(time.toLocalDate()).isPresent() || loadHallOccupancyPort.getByDateTime(time).isEmpty()) {
            return false;
        }

        int reservedPlaces = loadHallOccupancyPort.getByDateTime(time).get().getReservedPlaces();
        return reservedPlaces < bookingConfig.getHallMaxCapacity();
    }

    private boolean isOnlyOneBooking(LocalDateTime time) {
        UUID userId = loadAuthorizationInfoPort.getCurrentUser().getId();
        List<BookingModel> bookingModels = loadBookingPort.findAllIncludingTime(time);
        return bookingModels.stream().noneMatch(b -> b.getUserId().equals(userId)
                && b.getStatus() == BookingStatus.CONFIRMED);
    }

    public Set<Pair<LocalDateTime, LocalDateTime>> findClosestAvailableTimes(UUID areaId) {
        LocalDate currDate = LocalDate.now(bookingConfig.getZoneId());
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

    public Page<BookingModel> findWithFilters(
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable,
            UUID areaId,
            UUID userId
    ) {
        return loadBookingPort.findWithFilters(startDate, endDate, pageable, areaId, userId);
    }

    public BookingModel updateById(
            UUID bookingId,
            UUID userId,
            UUID areaId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            BookingStatus status
    ) {
        Optional<BookingModel> bookingOpt = loadBookingPort.findById(bookingId);
        Optional<AreaModel> areaOpt = loadAreaPort.findById(areaId);
        Optional<UserModel> userOpt = loadUserPort.findById(userId);
        if (bookingOpt.isEmpty() || userOpt.isEmpty() || areaOpt.isEmpty()) {
            throw new NoSuchElementException();
        }
        BookingModel model = bookingOpt.get();
        model.setAreaModel(areaOpt.get());
        model.setUserModel(userOpt.get());

        if (startTime == null || startTime.isAfter(endTime)) {
            throw new IllegalArgumentException(
                    "startTime should be not empty and be before endTime"
            );
        }

        model.setStartTime(startTime);
        model.setEndTime(endTime);
        model.setStatus(status);

        return saveBookingPort.save(model);
    }

    public void deleteById(UUID bookingId) {
        deleteBookingPort.deleteById(bookingId);
    }

}