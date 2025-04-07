package ru.tbank.bookit.book_it_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.tbank.bookit.book_it_backend.model.HallOccupancy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HallOccupancyRepository extends JpaRepository<HallOccupancy, LocalDateTime> {
    @Query("SELECT SUM(h.reservedPlaces) FROM HallOccupancy h WHERE " +
            "FUNCTION('YEAR', h.dateTime) = FUNCTION('YEAR', :date) AND " +
            "FUNCTION('MONTH', h.dateTime) = FUNCTION('MONTH', :date) AND " +
            "FUNCTION('DAY', h.dateTime) = FUNCTION('DAY', :date)")
    Optional<Integer> countReservedPlacesByDate(@Param("date") LocalDate date);

    @Query("SELECT h FROM HallOccupancy h WHERE DATE(h.dateTime) = :date")
    List<HallOccupancy> findByDate(@Param("date") LocalDate date);
}