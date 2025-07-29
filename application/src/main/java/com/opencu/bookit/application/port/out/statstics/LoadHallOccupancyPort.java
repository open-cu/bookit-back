package com.opencu.bookit.application.port.out.statstics;

import com.opencu.bookit.domain.model.statistics.HallOccupancyModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LoadHallOccupancyPort {
    Optional<Integer> countReservedPlacesByDate(LocalDate date);
    List<HallOccupancyModel> findByDate(LocalDate date);
    Optional<HallOccupancyModel> findById(LocalDateTime currHour);
    Optional<HallOccupancyModel> getByDateTime(LocalDateTime time);
}
