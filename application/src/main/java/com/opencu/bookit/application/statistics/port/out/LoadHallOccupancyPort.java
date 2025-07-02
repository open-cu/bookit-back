package com.opencu.bookit.application.statistics.port.out;

import com.opencu.bookit.domain.model.statistics.HallOccupancy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LoadHallOccupancyPort {
    Optional<Integer> countReservedPlacesByDate(LocalDate date);
    List<HallOccupancy> findByDate(LocalDate date);
    Optional<HallOccupancy> findById(LocalDateTime currHour);
    HallOccupancy getByDateTime(LocalDateTime time);
}
