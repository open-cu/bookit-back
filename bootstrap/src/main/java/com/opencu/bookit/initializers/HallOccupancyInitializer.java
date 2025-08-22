package com.opencu.bookit.initializers;

import com.opencu.bookit.adapter.out.persistence.entity.HallOccupancyEntity;
import com.opencu.bookit.adapter.out.persistence.repository.HallOccupancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class HallOccupancyInitializer implements ApplicationRunner {

    private final HallOccupancyRepository hallOccupancyRepository;

    @Value("${booking.max-days-forward}")
    private int maxDaysForward;

    @Value("${booking.start-work}")
    private int startWork;

    @Value("${booking.end-work}")
    private int endWork;

    @Value("${booking.zone-id}")
    private ZoneId zoneId;

    @Override
    public void run(ApplicationArguments args) {
        initializeHallOccupancy();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void updateHallOccupancy() {
        LocalDate lastDate = findLastDate();

        LocalDate nextDate = lastDate.plusDays(1);
        createEntriesForDate(nextDate);
    }

    private void initializeHallOccupancy() {
        LocalDate today = LocalDate.now(zoneId);
        LocalDate endDate = today.plusDays(maxDaysForward);

        for (LocalDate date = today; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<HallOccupancyEntity> existingEntries = hallOccupancyRepository.findByDate(date);

            if (existingEntries.isEmpty()) {
                createEntriesForDate(date);
            }
        }
    }

    private void createEntriesForDate(LocalDate date) {
        for (int hour = startWork; hour <= endWork - 1; hour++) {
            LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.of(hour, 0));

            if (hallOccupancyRepository.findById(dateTime).isEmpty()) {
                HallOccupancyEntity occupancy = new HallOccupancyEntity();
                occupancy.setDateTime(dateTime);
                occupancy.setReservedPlaces(0);
                hallOccupancyRepository.save(occupancy);
            }
        }
    }

    private LocalDate findLastDate() {
        return hallOccupancyRepository.findAll().stream()
                .map(HallOccupancyEntity::getDateTime)
                .map(LocalDateTime::toLocalDate)
                .distinct()
                .sorted((d1, d2) -> d2.compareTo(d1))
                .findFirst()
                .orElse(LocalDate.now(zoneId));
    }
}