package com.opencu.bookit.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Represents an exception or override to the regular weekly schedule for a specific date.
 * <p>
 * This entity is used for public holidays, special events, or any day when the
 * working hours differ from the default {@link WeeklySchedule}.
 * An entry in this table for a given date takes precedence over the weekly rule.
 */
@Entity
@Table(name = "SCHEDULE_OVERRIDES")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ScheduleOverride {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "override_date", nullable = false, unique = true)
    private LocalDate overrideDate;

    @Column(name = "is_day_off", nullable = false)
    private boolean isDayOff = false;

    /**
     * The special opening time for this date.
     * Null if it's a day off.
     */
    @Column(name = "opening_time")
    private LocalTime openingTime;

    /**
     * The special closing time for this date.
     * Null if it's a day off.
     */
    @Column(name = "closing_time")
    private LocalTime closingTime;

    @Column(name = "description", length = 255)
    private String description;

    /**
     * Ensures uniform appearance of records in the database.
     * If it's a day off, opening and closing times are set to null.
     * If it's a working day, both times must be non-null.
     */
    @PrePersist
    @PreUpdate
    private void validateTimes() {
        if (isDayOff) {
            openingTime = null;
            closingTime = null;
        } else {
            if (openingTime == null || closingTime == null) {
                throw new IllegalStateException("Opening and closing times must be set for a working day override.");
            }
            if (openingTime.isAfter(closingTime)) {
                throw new IllegalStateException("Opening time must be before closing time.");
            }
        }
    }
}