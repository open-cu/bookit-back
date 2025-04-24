package ru.tbank.bookit.book_it_backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.util.Pair;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateBookingRequest {
    private UUID userId;
    private UUID areaId;
    private Set<Pair<LocalDateTime, LocalDateTime>> timePeriods;
    private int quantity;
}
