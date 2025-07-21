package com.opencu.bookit.application.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "booking")
public class BookingConfig {
    private int maxDaysForward;
    private long startWork;
    private long endWork;
    private int hallMaxCapacity;
    private ZoneId zoneId;
}