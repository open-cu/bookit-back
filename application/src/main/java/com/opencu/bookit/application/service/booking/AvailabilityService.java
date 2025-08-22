package com.opencu.bookit.application.service.booking;

import com.opencu.bookit.application.config.BookingConfig;
import com.opencu.bookit.application.exception.FeatureUnavailableException;
import com.opencu.bookit.application.feature.AppFeatures;
import com.opencu.bookit.application.port.out.area.LoadAreaPort;
import com.opencu.bookit.application.port.out.booking.LoadBookingPort;
import com.opencu.bookit.application.port.out.schedule.LoadNonWorkingDaySchedulePort;
import com.opencu.bookit.application.port.out.statstics.LoadHallOccupancyPort;
import com.opencu.bookit.application.port.out.user.LoadAuthorizationInfoPort;
import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.area.AreaType;
import com.opencu.bookit.domain.model.booking.BookingModel;
import com.opencu.bookit.domain.model.booking.BookingStatus;
import com.opencu.bookit.domain.model.statistics.HallOccupancyModel;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AvailabilityService {
    private final LoadAreaPort loadAreaPort;
    private final LoadBookingPort loadBookingPort;
    private final LoadNonWorkingDaySchedulePort loadNonWorkingDaySchedulePort;
    private final BookingConfig bookingConfig;
    private final LoadHallOccupancyPort loadHallOccupancyPort;
    private final LoadAuthorizationInfoPort loadAuthorizationInfoPort;

    public AvailabilityService(LoadAreaPort loadAreaPort,
                               LoadBookingPort loadBookingPort,
                               LoadNonWorkingDaySchedulePort loadNonWorkingDaySchedulePort,
                               BookingConfig bookingConfig, LoadHallOccupancyPort loadHallOccupancyPort, LoadAuthorizationInfoPort loadAuthorizationInfoPort) {
        this.loadAreaPort = loadAreaPort;
        this.loadBookingPort = loadBookingPort;
        this.loadNonWorkingDaySchedulePort = loadNonWorkingDaySchedulePort;
        this.bookingConfig = bookingConfig;
        this.loadHallOccupancyPort = loadHallOccupancyPort;
        this.loadAuthorizationInfoPort = loadAuthorizationInfoPort;
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

            AreaModel area = loadAreaPort.findById(areaId.get()).orElseThrow(() -> new NoSuchElementException("Area not found with id: " + areaId.get()));

            if (!AppFeatures.BOOKING_MEETING_SPACES.isActive() && !area.getType().equals(AreaType.WORKPLACE)) {
                throw new FeatureUnavailableException("Booking meeting spaces is not enabled in the application features.");
            }

            addFreeTimes(availableTime, date, bookingModels, area);
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

    private boolean isAreaAvailable(LocalDateTime currHour, List<BookingModel> bookingModels, AreaModel areaModel, Optional<BookingModel> excludeBooking) {
        excludeBooking.ifPresent(bookingModel -> bookingModels.removeIf(b -> b.getId().equals(bookingModel.getId())));
        if (areaModel.getType() != AreaType.WORKPLACE) {
            UUID areaId = areaModel.getId();
            return bookingModels.stream().noneMatch(b -> bookingIncludeHour(currHour, b) &&
                    b.getAreaId().equals(areaId));
        } else {
            Optional<HallOccupancyModel> hallOccupancy = loadHallOccupancyPort.findById(currHour);
            UUID userId = loadAuthorizationInfoPort.getCurrentUser().getId();
            if (hallOccupancy.isPresent() && bookingModels.stream().noneMatch(b -> b.getUserId().equals(userId)
                    && bookingIncludeHour(currHour, b))) {
                if (excludeBooking.isPresent() && excludeBooking.get().getAreaId().equals(areaModel.getId())
                && bookingIncludeHour(currHour, excludeBooking.get())) {
                    return hallOccupancy.get().getReservedPlaces() - 1 < bookingConfig.getHallMaxCapacity();
                }
                return hallOccupancy.get().getReservedPlaces() < bookingConfig.getHallMaxCapacity();
            }
        }
        return false;
    }

    public boolean isAreaAvailable(UUID areaId, Set<LocalDateTime> timeSlots, Optional<BookingModel> excludeBooking) {
        AreaModel area = loadAreaPort.findById(areaId).orElseThrow(() -> new NoSuchElementException("Area not found with id: " + areaId));
        for (LocalDateTime time : timeSlots) {
            List<BookingModel> bookingModels = loadBookingPort.findAllIncludingTime(time);
            if (bookingModels.isEmpty()) {
                continue;
            }

            if (!isAreaAvailable(time, bookingModels, area, excludeBooking)) {
                return false;
            }
        }
        return true;
    }


    private List<LocalDate> findHallAvailableDates() {
        List<LocalDate> availableDates = new ArrayList<>();
        LocalDate today = LocalDate.now(bookingConfig.getZoneId());
        for (int i = 0; i <= 4; ++i) {
            LocalDate date = today.plusDays(i);
                if (loadNonWorkingDaySchedulePort.findByDate(date).isPresent()) {
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
            if (loadNonWorkingDaySchedulePort.findByDate(date).isEmpty()) {
                availableDates.add(date);
            }
        }
        return availableDates;
    }

    public List<UUID> findAvailableAreas(Set<LocalDateTime> requestedTimes) {
        HashSet<UUID> availableAreas = new HashSet<>();
        if (AppFeatures.BOOKING_MEETING_SPACES.isActive()) {
            availableAreas.addAll(loadAreaPort.findAll().stream().map(AreaModel::getId)
                                                       .collect(Collectors.toCollection(HashSet::new)));
            for (LocalDateTime time : requestedTimes) {
                List<BookingModel> bookingModels = loadBookingPort.findAllIncludingTime(time);
                Set<UUID> bookedAreaIds = bookingModels.stream().filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                                                       .map(BookingModel::getAreaId).collect(Collectors.toSet());
                availableAreas.removeAll(bookedAreaIds);
            }
        }
        UUID workplaceId = loadAreaPort.findByType(AreaType.WORKPLACE).getFirst().getId();
        availableAreas.remove(workplaceId);
        if (requestedTimes.stream().allMatch(this::isWorkplaceAvailable)) {
            availableAreas.add(workplaceId);
        }

        return availableAreas.stream().toList();
    }

    private boolean isWorkplaceAvailable(LocalDateTime time) {

        if (loadNonWorkingDaySchedulePort.findByDate(time.toLocalDate()).isPresent() || loadHallOccupancyPort.getByDateTime(time).isEmpty()) {
            return false;
        }

        int reservedPlaces = loadHallOccupancyPort.getByDateTime(time).get().getReservedPlaces();
        return reservedPlaces < bookingConfig.getHallMaxCapacity();
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

    private void addFreeTimes(List<List<Pair<LocalDateTime, LocalDateTime>>> availableTime, LocalDate date, List<BookingModel> bookingModels, AreaModel areaModel)
    {
        for (long i = bookingConfig.getStartWork(); i < bookingConfig.getEndWork(); ++i) {
            LocalDateTime currHour = date.atTime((int)i, 0);
            LocalDateTime nextHour = currHour.plusHours(1);
            if (isAreaAvailable(currHour, bookingModels, areaModel, Optional.empty())) {
                Pair<LocalDateTime, LocalDateTime> pair = Pair.of(currHour, nextHour);
                List<Pair<LocalDateTime, LocalDateTime>> addList = pair.getFirst().getHour() < 12 ? availableTime.get(0) : pair.getFirst().getHour() < 18 ? availableTime.get(1) : availableTime.get(2);
                LocalDateTime now = LocalDateTime.now(bookingConfig.getZoneId());
                if (!addList.contains(pair) && (now.getHour() <= pair.getFirst().getHour() || now.getDayOfYear() < pair.getFirst().getDayOfYear())) {
                    addList.addLast(pair);
                }
            }
        }
    }


    public List<LocalDate> findAvailableDates(Optional<UUID> areaId) {
        if (areaId.isPresent()) {
            AreaModel area = loadAreaPort.findById(areaId.get()).orElseThrow(() -> new NoSuchElementException("Area not found with id: " + areaId.get()));
            AreaType type = area.getType();
            if (!AppFeatures.BOOKING_MEETING_SPACES.isActive() && !type.equals(AreaType.WORKPLACE)) {
                throw new FeatureUnavailableException("Booking meeting spaces is not enabled in the application features.");
            }
            if(type.equals(AreaType.WORKPLACE)) {
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

            if (loadNonWorkingDaySchedulePort.findByDate(date).isPresent()) {
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

    private boolean bookingIncludeHour(LocalDateTime currHour, BookingModel b) {
        return currHour.isBefore(b.getEndTime()) && currHour.plusHours(1).isAfter(b.getStartTime());
    }
}
