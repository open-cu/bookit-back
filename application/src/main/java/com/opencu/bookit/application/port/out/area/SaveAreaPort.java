package com.opencu.bookit.application.port.out.area;

import com.opencu.bookit.domain.model.area.AreaModel;

import java.util.List;

public interface SaveAreaPort {
    void saveAll(List<AreaModel> openSpace);
}
