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
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUserId() {
        return userId;
    }
    public String getAreaId() {
        return areaId;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public int getQuantity() {
        return quantity;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}