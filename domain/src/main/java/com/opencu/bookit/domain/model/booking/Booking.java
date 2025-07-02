package com.opencu.bookit.domain.model.booking;

import com.opencu.bookit.domain.model.area.Area;
import com.opencu.bookit.domain.model.user.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private UUID id;
    private User user;
    private Area area;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int quantity;
    private BookingStatus status;
    private LocalDateTime createdAt;

    public UUID getAreaId() {
        return area != null ? area.getId() : null;
    }

    public UUID getUserId() {return user != null ? user.getId() : null;}
}