package com.opencu.bookit.domain.model.booking;

import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.user.UserModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingModel {
    private UUID id;
    private UserModel userModel;
    private AreaModel areaModel;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int quantity;
    private BookingStatus status;
    private LocalDateTime createdAt;

    public UUID getAreaId() {
        return areaModel != null ? areaModel.getId() : null;
    }

    public UUID getUserId() {return userModel != null ? userModel.getId() : null;}
}