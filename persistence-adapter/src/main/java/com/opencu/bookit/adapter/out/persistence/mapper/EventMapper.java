package com.opencu.bookit.adapter.out.persistence.mapper;

import com.opencu.bookit.adapter.out.persistence.entity.EventEntity;
import com.opencu.bookit.domain.model.event.EventModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class, AreaMapper.class})
public interface EventMapper {

    @Mapping(target = "userModels", source = "users")
    @Mapping(target = "formats", source = "formats")
    @Mapping(target = "times", source = "times")
    @Mapping(target = "participationFormats", source = "participationFormats")
    @Mapping(target = "areaModel", source = "area")
    EventModel toModel(EventEntity entity);

    @Mapping(target = "users", source = "userModels")
    @Mapping(target = "formats", source = "formats")
    @Mapping(target = "times", source = "times")
    @Mapping(target = "participationFormats", source = "participationFormats")
    @Mapping(target = "area", source = "areaModel")
    EventEntity toEntity(EventModel model);

    List<EventModel> toModelList(List<EventEntity> entities);
}