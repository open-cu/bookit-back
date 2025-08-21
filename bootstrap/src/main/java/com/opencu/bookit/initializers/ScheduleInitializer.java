package com.opencu.bookit.initializers;

import com.opencu.bookit.application.port.out.schedule.LoadNonWorkingDaySchedulePort;
import com.opencu.bookit.application.port.out.schedule.SaveSchedulePort;
import com.opencu.bookit.domain.model.schedule.DayStatus;
import com.opencu.bookit.domain.model.schedule.ScheduleModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class ScheduleInitializer implements ApplicationRunner {

    private final LoadNonWorkingDaySchedulePort loadNonWorkingDaySchedulePort;
    private final SaveSchedulePort saveSchedulePort;

    @Value("${booking.zone-id}")
    private ZoneId zoneId;

    @Override
    public void run(ApplicationArguments args) {
        initializeScheduleForCurrentMonth();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void updateScheduleForNextDay() {
        LocalDate today = LocalDate.now(zoneId);
        LocalDate futureDate = today.plusDays(30);

        addDayIfWeekend(futureDate);
    }

    private void addDayIfWeekend(LocalDate date) {
        if (loadNonWorkingDaySchedulePort.findById(date).isEmpty()) {
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
                ScheduleModel schedule = new ScheduleModel();
                schedule.setDay_off(date);
                schedule.setTag(DayStatus.WEEKEND);
                schedule.setDescription("Выходной день");
                saveSchedulePort.save(schedule);
            }
        }
    }

    private void initializeScheduleForCurrentMonth() {
        addWeekends(LocalDate.now(zoneId));
    }

    private void addWeekends(LocalDate date) {
        for (int i = 0; i < 30; i++) {
            addDayIfWeekend(date);
            date = date.plusDays(1);
        }
    }
}