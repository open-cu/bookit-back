package com.opencu.bookit.application.feature;

import org.togglz.core.Feature;
import org.togglz.core.annotation.EnabledByDefault;
import org.togglz.core.context.FeatureContext;

public enum AppFeatures implements Feature {

    @EnabledByDefault
    EMAIL_NOTIFICATIONS,

    TELEGRAM_NOTIFICATIONS;

    public boolean isActive() {
        return FeatureContext.getFeatureManager().isActive(this);
    }
}