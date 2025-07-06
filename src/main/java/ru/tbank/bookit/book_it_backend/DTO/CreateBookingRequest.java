package ru.tbank.bookit.book_it_backend.DTO;

import org.springframework.data.util.Pair;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record CreateBookingRequest(
        UUID userId,
        UUID areaId,
        Set<Pair<LocalDateTime, LocalDateTime>> timePeriods,
        int quantity) {}

