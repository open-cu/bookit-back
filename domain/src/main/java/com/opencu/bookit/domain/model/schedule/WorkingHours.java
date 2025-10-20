package com.opencu.bookit.domain.model.schedule;

import java.time.LocalTime;
import java.util.Objects;

/**
 * A Value Object representing the opening and closing times for a single day.
 * <p>
 * This record enforces the invariant that the opening time must not be after the closing time,
 * ensuring that only valid working hour ranges can be created within the domain.
 */
public record WorkingHours(LocalTime openingTime, LocalTime closingTime) {

    /**
     * Compact constructor to enforce business rules upon creation.
     *
     * @throws NullPointerException if openingTime or closingTime is null.
     * @throws IllegalArgumentException if openingTime is after closingTime.
     */
    public WorkingHours {
        Objects.requireNonNull(openingTime, "Opening time must not be null");
        Objects.requireNonNull(closingTime, "Closing time must not be null");
        if (openingTime.isAfter(closingTime)) {
            throw new IllegalArgumentException("Opening time must be before closing time.");
        }
    }
}