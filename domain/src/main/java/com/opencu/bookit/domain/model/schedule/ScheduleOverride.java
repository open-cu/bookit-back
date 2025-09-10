package com.opencu.bookit.domain.model.schedule;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a domain concept of an exception to the regular weekly schedule.
 * <p>
 * This model is used for specific dates like public holidays or special events where
 * the working hours differ from the default weekly template. An override for a given
 * date always takes precedence over the regular {@link WeeklySchedule}.
 * <p>
 * The presence or absence of {@code workingHours} indicates whether the coworking
 * is open or closed on this specific date.
 */
public record ScheduleOverride(
    UUID id,
    LocalDate overrideDate,
    Optional<WorkingHours> workingHours,
    Optional<String> description
) {
    public boolean isDayOff() {
        return workingHours.isEmpty();
    }
}