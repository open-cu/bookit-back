package com.opencu.bookit.application.config.port.out;

public interface LoadBookingConfigurationPort {
    int getMaxDaysForward();
    long getEndWork();
    long getStartWork();
    int getHallMaxCapacity();
}
