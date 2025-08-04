package com.opencu.bookit.adapter.in.web.mapper;

import com.opencu.bookit.adapter.in.web.dto.response.AreaResponse;
import com.opencu.bookit.application.service.photo.PhotoService;
import com.opencu.bookit.domain.model.area.AreaFeature;
import com.opencu.bookit.domain.model.area.AreaModel;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class AreaResponseMapper {
    private final PhotoService service;

    public AreaResponseMapper(PhotoService service) {
        this.service = service;
    }

    public AreaResponse toAreaResponse(AreaModel area) throws IOException {
        return new AreaResponse(
                area.getId(),
                area.getName(),
                area.getDescription(),
                area.getType(),
                Set.copyOf(area.getFeatures()),
                service.getImagesFromKeys(area.getKeys()),
                area.getCapacity()
        );
    }

    public List<AreaResponse> toAreaResponseList(List<AreaModel> areas) throws IOException {
        List<AreaResponse> areaResponseList = new ArrayList<>();
        for (AreaModel area : areas) {
            areaResponseList.add(toAreaResponse(area));
        }
        return areaResponseList;
    }

    public Set<String> mapFeatures(AreaFeature features) {
        if (features == null) return null;
        return Set.of(features.name());
    }
}