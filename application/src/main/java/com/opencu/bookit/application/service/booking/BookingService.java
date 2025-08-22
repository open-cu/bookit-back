package com.opencu.bookit.application.service.booking;

import com.opencu.bookit.application.config.BookingConfig;
import com.opencu.bookit.application.exception.FeatureUnavailableException;
import com.opencu.bookit.application.feature.AppFeatures;
import com.opencu.bookit.application.port.out.area.LoadAreaPort;
import com.opencu.bookit.application.port.in.booking.CRUDBookingUseCase;
import com.opencu.bookit.application.port.out.booking.DeleteBookingPort;
import com.opencu.bookit.application.port.out.booking.LoadBookingPort;
import com.opencu.bookit.application.port.out.booking.SaveBookingPort;
import com.opencu.bookit.application.port.out.user.LoadAuthorizationInfoPort;
import com.opencu.bookit.application.port.out.user.LoadUserPort;
import com.opencu.bookit.application.port.out.statstics.LoadHallOccupancyPort;
import com.opencu.bookit.application.port.out.statstics.SaveHallOccupancyPort;
import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.area.AreaType;
import com.opencu.bookit.domain.model.booking.BookingModel;
import com.opencu.bookit.domain.model.booking.BookingStatus;
import com.opencu.bookit.domain.model.booking.TimeTag;
import com.opencu.bookit.domain.model.booking.ValidationRule;
import com.opencu.bookit.domain.model.statistics.HallOccupancyModel;
import com.opencu.bookit.domain.model.user.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BookingService {
    private final LoadBookingPort loadBookingPort;
    private final SaveBookingPort saveBookingPort;
    private final DeleteBookingPort deleteBookingPort;
    private final LoadHallOccupancyPort loadHallOccupancyPort;
    private final SaveHallOccupancyPort saveHallOccupancyPort;
    private final LoadAreaPort loadAreaPort;
    private final LoadUserPort loadUserPort;
    private final LoadAuthorizationInfoPort loadAuthorizationInfoPort;

    private final BookingConfig bookingConfig;
    private final BookingValidationService bookingValidationService;

    @Autowired
    public BookingService(LoadBookingPort loadBookingPort, SaveBookingPort saveBookingPort, DeleteBookingPort deleteBookingPort,
                          LoadHallOccupancyPort loadHallOccupancyPort, SaveHallOccupancyPort saveHallOccupancyPort,
                          BookingConfig bookingConfig, LoadAreaPort loadAreaPort, LoadUserPort loadUserPort,
                          LoadAuthorizationInfoPort loadAuthorizationInfoPort, BookingValidationService bookingValidationService) {
        this.loadBookingPort = loadBookingPort;
        this.saveBookingPort = saveBookingPort;
        this.deleteBookingPort = deleteBookingPort;
        this.loadHallOccupancyPort = loadHallOccupancyPort;
        this.saveHallOccupancyPort = saveHallOccupancyPort;
        this.bookingConfig = bookingConfig;
        this.loadAreaPort = loadAreaPort;
        this.loadUserPort = loadUserPort;
        this.loadAuthorizationInfoPort = loadAuthorizationInfoPort;
        this.bookingValidationService = bookingValidationService;
    }

    public Optional<BookingModel> findBooking(UUID bookingId) {
        return loadBookingPort.findById(bookingId);
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
    public List<BookingModel> createBooking(CRUDBookingUseCase.CreateBookingCommand createBookingCommand, Set<ValidationRule> validationRules) {
        UserModel userModel = loadUserPort.findById(createBookingCommand.userId())
                                          .orElseThrow(() -> new NoSuchElementException("User not found with id: " + createBookingCommand.userId()));

        AreaModel areaModel = loadAreaPort.findById(createBookingCommand.areaId())
                                          .orElseThrow(() -> new NoSuchElementException("Area not found with id: " + createBookingCommand.areaId()));

        if (!AppFeatures.BOOKING_MEETING_SPACES.isActive() && !areaModel.getType().equals(AreaType.WORKPLACE)) {
            throw new FeatureUnavailableException("Booking meeting spaces is not enabled in the application features.");
        }

        bookingValidationService.validateBooking(createBookingCommand, validationRules);

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
    public BookingModel updateBooking(UUID bookingId, CRUDBookingUseCase.UpdateBookingQuery request, Set<ValidationRule> rulesToApply) {
        UUID areaId = request.areaId();
        LocalDateTime startTime = request.startTime();
        LocalDateTime endTime = request.endTime();
        BookingModel bookingModel = loadBookingPort.findById(bookingId)
                                                   .orElseThrow(() -> new NoSuchElementException("Booking not found with id: " + bookingId));

        bookingValidationService.validateBooking(bookingId, request, rulesToApply);

        UUID userId = loadAuthorizationInfoPort.getCurrentUser().getId();

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

        if (!AppFeatures.BOOKING_MEETING_SPACES.isActive() && !areaOpt.get().getType().equals(AreaType.WORKPLACE)) {
            throw new FeatureUnavailableException("Booking meeting spaces is not enabled in the application features.");
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

    public void deleteBookingAccordingToIndirectParameters(UUID userId, UUID areaId, LocalDateTime startTime, LocalDateTime endTime) {
        deleteBookingPort.deleteBookingAccordingToIndirectParameters(userId, areaId, startTime, endTime);
    }

    @Transactional
    public BookingModel updateBookingAccordingToIndirectParameters(CRUDBookingUseCase.UpdateBookingQuery updateBookingQuery, Set<ValidationRule> rulesToApply, UUID userId, UUID areaId, LocalDateTime startTime, LocalDateTime endTime) {
        BookingModel bookingModelOptional = loadBookingPort.findByIndirectParameters(userId, areaId, startTime, endTime).orElseThrow(() -> new NoSuchElementException("Booking of user:" + userId + "not found"));
        return updateBooking(bookingModelOptional.getId(), updateBookingQuery, rulesToApply);
    }
}