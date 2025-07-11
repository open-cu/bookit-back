package com.opencu.bookit.adapter.out.persistence.repository;

import com.opencu.bookit.adapter.out.persistence.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity, LocalDate> {
    @Query("SELECT s FROM ScheduleEntity s WHERE s.day_off = :date")
    Optional<ScheduleEntity> findByDate(LocalDate date);

    default Optional<String> getNonWorkingReason(LocalDate date) {
        return findById(date)
                .map(scheduleEntity -> scheduleEntity.getTag().getDescription() +
                        (scheduleEntity.getDescription() != null ?
                                ". " + scheduleEntity.getDescription() : ""));
    }
}