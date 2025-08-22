package com.opencu.bookit.application.exception;

import com.opencu.bookit.application.feature.AppFeatures;

public class FeatureUnavailableException extends RuntimeException {

    public FeatureUnavailableException(AppFeatures feature) {
        super(String.format("Feature '%s' is currently unavailable.", feature.name()));
    }

    public FeatureUnavailableException(String message) {
        super(message);
    }
}