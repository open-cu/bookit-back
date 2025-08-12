package com.opencu.bookit.application.port.out.schedule;

import com.opencu.bookit.domain.model.schedule.ScheduleModel;

import java.time.LocalDate;
import java.util.Optional;

public interface LoadNonWorkingDaySchedulePort {
    Optional<ScheduleModel> findByDate(LocalDate date);
    Optional<ScheduleModel> findById(LocalDate date);
}