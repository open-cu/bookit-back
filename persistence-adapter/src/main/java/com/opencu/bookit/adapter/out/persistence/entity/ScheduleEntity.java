package com.opencu.bookit.adapter.out.persistence.entity;

import com.opencu.bookit.domain.model.schedule.DayStatus;
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
public class ScheduleEntity {
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