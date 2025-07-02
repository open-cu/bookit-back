package com.opencu.bookit.application.statistics.port.out;

import com.opencu.bookit.domain.model.statistics.HallOccupancy;

public interface SaveHallOccupancyPort {
    void save(HallOccupancy hallOccupancy);
}