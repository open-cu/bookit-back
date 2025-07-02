package com.opencu.bookit.application.schedule.port.out;

import com.opencu.bookit.domain.model.schedule.Schedule;

import java.time.LocalDate;
import java.util.Optional;

public interface LoadSchedulePort {
    Optional<Schedule> findByDate(LocalDate date);
}