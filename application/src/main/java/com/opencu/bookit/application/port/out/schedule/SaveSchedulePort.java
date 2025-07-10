package com.opencu.bookit.application.port.out.schedule;

import com.opencu.bookit.domain.model.schedule.ScheduleModel;

public interface SaveSchedulePort {
    ScheduleModel save(ScheduleModel schedule);
}

