package com.opencu.bookit.domain.model.schedule;

import java.time.DayOfWeek;
import java.util.Optional;

/**
 * Represents the domain concept of a regular weekly schedule.
 * <p>
 * This model defines the default working hours for a specific day of the week.
 * A day can either be a working day, represented by present {@code workingHours},
 * or a day off, represented by an empty optional.
 */
public record WeeklySchedule(
    DayOfWeek dayOfWeek,
    Optional<WorkingHours> workingHours
) {
    public boolean isDayOff() {
        return workingHours.isEmpty();
    }
}