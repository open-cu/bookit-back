package com.opencu.bookit.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "HALL_OCCUPANCY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HallOccupancyEntity {
    @Id
    @Column(name = "date_time", nullable = false, unique = true)
    private LocalDateTime dateTime;

    @Column(name = "reserved_places", nullable = false)
    private int reservedPlaces;
}