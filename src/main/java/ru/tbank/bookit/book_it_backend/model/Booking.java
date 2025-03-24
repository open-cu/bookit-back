package ru.tbank.bookit.book_it_backend.model;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Booking {
    private String id;
    private String userId;
    private String areaId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int quantity;
    private String status;
    private LocalDateTime createdAt;
    
    public Booking(String userId, String areaId, LocalDateTime startTime, LocalDateTime endTime, int quantity) {
        this.userId = userId;
        this.areaId = areaId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.quantity = quantity;
    }
}