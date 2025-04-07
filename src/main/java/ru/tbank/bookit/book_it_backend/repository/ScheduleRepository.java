package ru.tbank.bookit.book_it_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.tbank.bookit.book_it_backend.model.Schedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, LocalDate> {
    @Query("SELECT s FROM Schedule s WHERE s.day_off = :date")
    Optional<Schedule> findByDate(LocalDate date);

    default Optional<String> getNonWorkingReason(LocalDate date) {
        return findById(date)
                .map(schedule -> schedule.getTag().getDescription() +
                        (schedule.getDescription() != null ?
                                ". " + schedule.getDescription() : ""));
    }

    @Query("SELECT s FROM Schedule s WHERE s.day_off BETWEEN :start AND :end")
    List<Schedule> findByDate(LocalDate start, LocalDate end);
}