package ru.tbank.bookit.book_it_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "HALL_OCCUPANCY",
        uniqueConstraints = @UniqueConstraint(columnNames = "date-time"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HallOccupancy {
    @Id
    @Column(name = "date-time", nullable = false, unique = true)
    private LocalDateTime dateTime;

    @Column(name = "reserved places", nullable = false)
    private Long reservedPlaces;
}