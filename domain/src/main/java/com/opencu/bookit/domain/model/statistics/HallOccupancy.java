package com.opencu.bookit.domain.model.statistics;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HallOccupancy {
    private LocalDateTime dateTime;
    private int reservedPlaces;
}