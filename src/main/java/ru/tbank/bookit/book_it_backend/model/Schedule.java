package ru.tbank.bookit.book_it_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "SCHEDULE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    @Id
    @Column( nullable = false, unique = true)
    private LocalDate day_off;

    @Column
    private String description;

    @Column
    private LocalTime start_time;

    @Column
    private LocalTime stop_time;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayStatus tag;
}