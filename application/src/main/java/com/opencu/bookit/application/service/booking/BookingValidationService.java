package com.opencu.bookit.application.service.booking;

import com.opencu.bookit.application.config.BookingConfig;
import com.opencu.bookit.application.port.in.booking.CRUDBookingUseCase;
import com.opencu.bookit.application.port.out.area.LoadAreaPort;
import com.opencu.bookit.application.port.out.booking.LoadBookingPort;
import com.opencu.bookit.application.port.out.schedule.LoadNonWorkingDaySchedulePort;
import com.opencu.bookit.application.port.out.user.LoadAuthorizationInfoPort;
import com.opencu.bookit.domain.model.booking.BookingModel;
import com.opencu.bookit.domain.model.booking.BookingStatus;
import com.opencu.bookit.domain.model.booking.ValidationRule;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BookingValidationService {
    private final LoadAreaPort loadAreaPort;
    private final LoadBookingPort loadBookingPort;
    private final LoadAuthorizationInfoPort loadAuthorizationInfoPort;
    private final LoadNonWorkingDaySchedulePort loadNonWorkingDaySchedulePort;

    private final AvailabilityService availabilityService;
    private final BookingConfig bookingConfig;

    public void validateBooking(CRUDBookingUseCase.CreateBookingCommand command, Set<ValidationRule> rulesToApply) {
        if (rulesToApply == null || rulesToApply.isEmpty()) {
            return;
        }

        if (rulesToApply.contains(ValidationRule.VALIDATE_USER_OWNERSHIP)) {
            if (command.userId() == null) {
                throw new IllegalArgumentException("User ID must be provided for booking validation.");
            }
            if (command.userId() != loadAuthorizationInfoPort.getCurrentUser().getId()) {
                throw new IllegalArgumentException("User ID does not match the current user.");
            }
        }

        for (Pair<LocalDateTime, LocalDateTime> time : command.timePeriods()) {
            LocalDateTime startTime = time.getFirst();
            LocalDateTime endTime = time.getSecond();

            if (rulesToApply.contains(ValidationRule.VALIDATE_TIME_RESTRICTIONS)) {
                if (command.timePeriods().isEmpty()) {
                    throw new IllegalArgumentException("There are no chosen time periods");
                }

                if (loadNonWorkingDaySchedulePort.findByDate(startTime.toLocalDate()).isPresent()) {
                    throw new IllegalArgumentException("Booking cannot be made on a non-working day.");
                }
                
                if (startTime.isAfter(endTime)) {
                    throw new IllegalArgumentException("Start time must be before end time.");
                }
                if (endTime.isBefore(LocalDateTime.now())) {
                    throw new IllegalArgumentException("End time must be in the future.");
                }
                if (startTime.toLocalDate().isBefore(LocalDate.now())) {
                    throw new IllegalArgumentException("Start time must be today or in the future.");
                }
                if (!startTime.toLocalDate().equals(endTime.toLocalDate())) {
                    throw new IllegalArgumentException("Booking time must be within the same day.");
                }
                if (startTime.getHour() < bookingConfig.getStartWork()) {
                    throw new IllegalArgumentException("Booking start time cannot be before the start of the workday.");
                }
                if (endTime.getHour() > bookingConfig.getEndWork()) {
                    throw new IllegalArgumentException("Booking end time cannot be after the end of the workday.");
                }
            }
            
            Set<LocalDateTime> timeSlots = getHourlySlots(time);
            if (rulesToApply.contains(ValidationRule.VALIDATE_USER_BOOKING_CONFLICTS)) {
                if (hasExistingBookings(command.userId(), timeSlots, Optional.empty())) {
                    throw new IllegalArgumentException("You already have a booking in the selected time period.");
                }
            }

            if (rulesToApply.contains(ValidationRule.VALIDATE_AREA_AVAILABILITY)) {
                if (!availabilityService.isAreaAvailable(command.areaId(), timeSlots, Optional.empty())) {
                        throw new IllegalArgumentException("Area is not available for booking in the selected time period.");
                }
            }

            // TO DO: check out if checking event availability is necessary
        }
    }

    private Set<LocalDateTime> getHourlySlots(Pair<LocalDateTime, LocalDateTime> time) {
        Set<LocalDateTime> slots = new HashSet<>();
        for (LocalDateTime t = time.getFirst(); t.isBefore(time.getSecond()); t = t.plusHours(1)) {
            slots.add(t);
        }
        return slots;
    }

    private boolean hasExistingBookings(UUID userId, Set<LocalDateTime> times, Optional<BookingModel> excludedBooking) {
        for (LocalDateTime time : times) {
            List<BookingModel> bookingModels = loadBookingPort.findAllIncludingTime(time);
            boolean hasBooking = bookingModels.stream()
                    .anyMatch(b -> b.getUserId().equals(userId) && b.getStatus() == BookingStatus.CONFIRMED &&
                                   (excludedBooking.isEmpty() || !b.getId().equals(excludedBooking.get().getId())));
            if (hasBooking) {
                return true;
            }
        }
        return false;
    }

    // TO DO: There's no ways to validate and update eventId when booking. We need to check this out.
    public void validateBooking(UUID bookingId, CRUDBookingUseCase.UpdateBookingQuery command, Set<ValidationRule> rulesToApply) {
        BookingModel bookingModel = loadBookingPort.findById(bookingId).orElseThrow(() -> new IllegalArgumentException("Booking with this id was not found"));

        Set<ValidationRule> modifiedRules = new HashSet<>(rulesToApply);

        Set<LocalDateTime> timeSlots = getHourlySlots(Pair.of(command.startTime(), command.endTime()));
        if (modifiedRules.contains(ValidationRule.VALIDATE_USER_BOOKING_CONFLICTS)) {
            modifiedRules.remove(ValidationRule.VALIDATE_USER_BOOKING_CONFLICTS);
            if (hasExistingBookings(bookingModel.getUserId(), timeSlots, Optional.of(bookingModel))) {
                throw new IllegalArgumentException("You already have a booking in the selected time period.");
            }
        }
        if (modifiedRules.contains(ValidationRule.VALIDATE_AREA_AVAILABILITY)) {
            modifiedRules.remove(ValidationRule.VALIDATE_AREA_AVAILABILITY);
            if (!availabilityService.isAreaAvailable(command.areaId(), timeSlots, Optional.of(bookingModel))) {
                throw new IllegalArgumentException("Area is not available for booking in the selected time period.");
            }
        }

        validateBooking(new CRUDBookingUseCase.CreateBookingCommand(
                bookingModel.getUserId(),
                command.areaId(),
                null,
                Set.of(Pair.of(command.startTime(), command.endTime())),
                1
        ), modifiedRules);
    }
}
