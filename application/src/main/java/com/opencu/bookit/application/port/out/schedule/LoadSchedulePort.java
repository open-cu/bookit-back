package com.opencu.bookit.application.port.out.schedule;

import com.opencu.bookit.domain.model.schedule.ScheduleModel;

import java.time.LocalDate;
import java.util.Optional;

public interface LoadSchedulePort {
    Optional<ScheduleModel> findByDate(LocalDate date);
}