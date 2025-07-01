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
            "EXTRACT(YEAR FROM CAST(h.dateTime AS timestamp)) = EXTRACT(YEAR FROM CAST(:date AS timestamp)) AND " +
            "EXTRACT(MONTH FROM CAST(h.dateTime AS timestamp)) = EXTRACT(MONTH FROM CAST(:date AS timestamp)) AND " +
            "EXTRACT(DAY FROM CAST(h.dateTime AS timestamp)) = EXTRACT(DAY FROM CAST(:date AS timestamp))")
    Optional<Integer> countReservedPlacesByDate(@Param("date") LocalDate date);

    @Query("SELECT h FROM HallOccupancy h WHERE " +
            "EXTRACT(YEAR FROM CAST(h.dateTime AS timestamp)) = EXTRACT(YEAR FROM CAST(:date AS timestamp)) AND " +
            "EXTRACT(MONTH FROM CAST(h.dateTime AS timestamp)) = EXTRACT(MONTH FROM CAST(:date AS timestamp)) AND " +
            "EXTRACT(DAY FROM CAST(h.dateTime AS timestamp)) = EXTRACT(DAY FROM CAST(:date AS timestamp))")
    List<HallOccupancy> findByDate(@Param("date") LocalDate date);
}