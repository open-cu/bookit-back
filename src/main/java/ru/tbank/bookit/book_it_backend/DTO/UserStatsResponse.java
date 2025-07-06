package ru.tbank.bookit.book_it_backend.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record UserStatsResponse(
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        long totalRegistrations,
        @JsonProperty("percentageChange")
        Double percentageChange
) {}
