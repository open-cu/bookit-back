package ru.tbank.bookit.book_it_backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "booking")
public class BookingConfig {
    private int maxDaysForward;
    private long startWork;
    private long endWork;
}