package ru.tbank.bookit.book_it_backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CreateBookingRequest {
    private String userId;
    private String areaId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int quantity;
}
