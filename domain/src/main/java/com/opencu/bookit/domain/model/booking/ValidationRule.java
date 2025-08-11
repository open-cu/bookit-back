package com.opencu.bookit.domain.model.booking;

import java.util.EnumSet;
import java.util.Set;

public enum ValidationRule {
    VALIDATE_AREA_AVAILABILITY,
    VALIDATE_USER_BOOKING_CONFLICTS,
    VALIDATE_USER_OWNERSHIP,
    VALIDATE_TIME_RESTRICTIONS;
}