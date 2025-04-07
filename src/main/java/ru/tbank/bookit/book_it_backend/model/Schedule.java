package ru.tbank.bookit.book_it_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "SCHEDULE",
        uniqueConstraints = @UniqueConstraint(columnNames = "day"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    @Id
    @Column(nullable = false, unique = true)
    private LocalDate day;

    @Column
    private String description;

    @Column
    private LocalTime start;

    @Column
    private LocalTime stop;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayStatus tag;

    public boolean isFullDayClosure() {
        return start == null && stop == null;
    }
}