package com.opencu.bookit.adapter.in.web.mapper;

import com.opencu.bookit.adapter.in.web.dto.response.AreaResponse;
import com.opencu.bookit.domain.model.area.AreaFeature;
import com.opencu.bookit.domain.model.area.AreaModel;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface AreaResponseMapper {

    @Mapping(target = "type", source = "type")
    @Mapping(target = "features", expression = "java(mapFeatures(area.getFeatures()))")
    AreaResponse toAreaResponse(AreaModel area);

    List<AreaResponse> toAreaResponseList(List<AreaModel> areas);
    default Set<String> mapFeatures(AreaFeature features) {
        if (features == null) return null;
        return Set.of(features.name());
    }
}