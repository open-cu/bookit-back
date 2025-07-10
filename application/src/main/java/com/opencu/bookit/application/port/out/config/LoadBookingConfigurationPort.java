package com.opencu.bookit.application.port.out.config;

public interface LoadBookingConfigurationPort {
    int getMaxDaysForward();
    long getEndWork();
    long getStartWork();
    int getHallMaxCapacity();
}
