package com.opencu.bookit.application.feature;

import org.togglz.core.Feature;
import org.togglz.core.annotation.EnabledByDefault;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum AppFeatures implements Feature {

    @EnabledByDefault
    EMAIL_NOTIFICATIONS,

    TELEGRAM_NOTIFICATIONS,

    @Label("Booking meeting rooms, lecture halls, etc., as well as displaying them in the list of available areas.")
    @EnabledByDefault
    BOOKING_MEETING_SPACES;

    public boolean isActive() {
        return FeatureContext.getFeatureManager().isActive(this);
    }
}