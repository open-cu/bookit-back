package ru.tbank.bookit.book_it_backend.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateBookingRequest(
        UUID areaId,
        LocalDateTime startTime,
        LocalDateTime endTime) {}