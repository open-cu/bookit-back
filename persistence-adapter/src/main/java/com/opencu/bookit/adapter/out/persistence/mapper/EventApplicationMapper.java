package com.opencu.bookit.adapter.out.persistence.mapper;

import com.opencu.bookit.adapter.out.persistence.entity.EventApplicationEntity;
import com.opencu.bookit.domain.model.event.EventApplicationModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class, EventMapper.class})
public interface EventApplicationMapper {

    @Mapping(target = "userModel", source = "user")
    @Mapping(target = "eventModel", source = "event")
    EventApplicationModel toModel(EventApplicationEntity entity);

    @Mapping(target = "user", source = "userModel")
    @Mapping(target = "event", source = "eventModel")
    EventApplicationEntity toEntity(EventApplicationModel model);

    List<EventApplicationModel> toModelList(List<EventApplicationEntity> entities);
}