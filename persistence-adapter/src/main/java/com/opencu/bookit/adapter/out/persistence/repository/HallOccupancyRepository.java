package com.opencu.bookit.adapter.out.persistence.repository;

import com.opencu.bookit.adapter.out.persistence.entity.HallOccupancyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HallOccupancyRepository extends JpaRepository<HallOccupancyEntity, LocalDateTime> {
    @Query("SELECT SUM(h.reservedPlaces) FROM HallOccupancyEntity h WHERE " +
            "EXTRACT(YEAR FROM CAST(h.dateTime AS timestamp)) = EXTRACT(YEAR FROM CAST(:date AS timestamp)) AND " +
            "EXTRACT(MONTH FROM CAST(h.dateTime AS timestamp)) = EXTRACT(MONTH FROM CAST(:date AS timestamp)) AND " +
            "EXTRACT(DAY FROM CAST(h.dateTime AS timestamp)) = EXTRACT(DAY FROM CAST(:date AS timestamp))")
    Optional<Integer> countReservedPlacesByDate(@Param("date") LocalDate date);

    @Query("SELECT h FROM HallOccupancyEntity h WHERE " +
            "EXTRACT(YEAR FROM CAST(h.dateTime AS timestamp)) = EXTRACT(YEAR FROM CAST(:date AS timestamp)) AND " +
            "EXTRACT(MONTH FROM CAST(h.dateTime AS timestamp)) = EXTRACT(MONTH FROM CAST(:date AS timestamp)) AND " +
            "EXTRACT(DAY FROM CAST(h.dateTime AS timestamp)) = EXTRACT(DAY FROM CAST(:date AS timestamp))")
    List<HallOccupancyEntity> findByDate(@Param("date") LocalDate date);
}