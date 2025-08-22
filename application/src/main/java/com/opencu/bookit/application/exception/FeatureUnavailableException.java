package com.opencu.bookit.application.exception;

import com.opencu.bookit.application.feature.AppFeatures;

public class FeatureUnavailableException extends RuntimeException {

    private final String featureName;

    public FeatureUnavailableException(AppFeatures feature) {
        super(String.format("Feature '%s' is currently unavailable.", feature.name()));
        this.featureName = feature.name();
    }

    public String getFeatureName() {
        return featureName;
    }
}