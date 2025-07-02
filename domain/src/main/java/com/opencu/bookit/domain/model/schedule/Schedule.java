package com.opencu.bookit.domain.model.schedule;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    private LocalDate day_off;
    private String description;
    private LocalTime start_time;
    private LocalTime stop_time;
    private DayStatus tag;
}