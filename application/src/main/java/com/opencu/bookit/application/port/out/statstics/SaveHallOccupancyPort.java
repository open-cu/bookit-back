package com.opencu.bookit.application.port.out.statstics;

import com.opencu.bookit.domain.model.statistics.HallOccupancyModel;

public interface SaveHallOccupancyPort {
    void save(HallOccupancyModel hallOccupancyModel);
}