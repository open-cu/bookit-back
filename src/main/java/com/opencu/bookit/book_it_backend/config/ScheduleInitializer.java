package ru.tbank.bookit.book_it_backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.tbank.bookit.book_it_backend.model.DayStatus;
import ru.tbank.bookit.book_it_backend.model.Schedule;
import ru.tbank.bookit.book_it_backend.repository.ScheduleRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Component
@Order(4)
@RequiredArgsConstructor
public class ScheduleInitializer implements ApplicationRunner {

    private final ScheduleRepository scheduleRepository;

    @Override
    public void run(ApplicationArguments args) {
        initializeScheduleForCurrentMonth();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void updateScheduleForNextDay() {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(30);

        addDayIfWeekend(futureDate);
    }

    private void addDayIfWeekend(LocalDate date) {
        if (scheduleRepository.findById(date).isEmpty()) {
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY ||
                    date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                Schedule schedule = new Schedule();
                schedule.setDay_off(date);
                schedule.setTag(DayStatus.WEEKEND);
                schedule.setDescription("Выходной день");
                scheduleRepository.save(schedule);
            }
        }
    }

    private void initializeScheduleForCurrentMonth() {
        addWeekends(LocalDate.now());
    }

    private void addWeekends(LocalDate date) {
        for (int i = 0; i < 30; i++) {
            addDayIfWeekend(date);
            date = date.plusDays(1);
        }
    }
}