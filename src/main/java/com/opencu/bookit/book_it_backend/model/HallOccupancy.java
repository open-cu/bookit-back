package ru.tbank.bookit.book_it_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "HALL_OCCUPANCY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HallOccupancy {
    @Id
    @Column(name = "date_time", nullable = false, unique = true)
    private LocalDateTime dateTime;

    @Column(name = "reserved_places", nullable = false)
    private int reservedPlaces;
}