package com.opencu.bookit.application.port.out.area;

import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.booking.BookingModel;

import java.util.List;
import java.util.Set;

public interface SaveAreaPort {
    List<AreaModel> saveAll(List<AreaModel> openSpace);
}
