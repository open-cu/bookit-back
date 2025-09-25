package com.opencu.bookit.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Represents a regular weekly schedule for the coworking space.
 * <p>
 * This entity defines the default working hours for each day of the week.
 * It uses the {@link DayOfWeek} enum as its natural primary key, ensuring
 * that there is exactly one, unique entry per day.
 * <p>
 * These rules can be overridden by {@link ScheduleOverrideEntity} for specific dates.
 */
@Entity
@Table(name = "WEEKLY_SCHEDULE")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class WeeklyScheduleEntity {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, updatable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "is_day_off", nullable = false)
    private boolean isDayOff = false;

    /**
     * The time the coworking space opens on this day.
     * Can be null if it's a day off.
     */
    @Column(name = "opening_time")
    private LocalTime openingTime;

    /**
     * The time the coworking space closes on this day.
     * Can be null if it's a day off.
     */
    @Column(name = "closing_time")
    private LocalTime closingTime;

    public WeeklyScheduleEntity(DayOfWeek dayOfWeek, boolean isDayOff, LocalTime openingTime, LocalTime closingTime) {
        this.dayOfWeek = dayOfWeek;
        this.isDayOff = isDayOff;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        validateTimes();
    }

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
                throw new IllegalStateException("Opening and closing times must be set for a working day. Day: " + dayOfWeek);
            }
        }
    }
}