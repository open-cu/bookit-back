package ru.tbank.bookit.book_it_backend.mapper;

import org.mapstruct.*;
import ru.tbank.bookit.book_it_backend.DTO.AreaResponse;
import ru.tbank.bookit.book_it_backend.model.Area;
import ru.tbank.bookit.book_it_backend.model.AreaFeature;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface AreaMapper {

    @Mapping(target = "type", source = "type")
    @Mapping(target = "features", expression = "java(mapFeatures(area.getFeatures()))")
    AreaResponse toAreaResponse(Area area);

    List<AreaResponse> toAreaResponseList(List<Area> areas);

    default Set<String> mapFeatures(AreaFeature features) {
        if (features == null) return null;
        return Set.of(features.name());
    }
}