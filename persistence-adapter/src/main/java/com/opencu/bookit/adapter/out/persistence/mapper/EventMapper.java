package com.opencu.bookit.adapter.out.persistence.mapper;

import com.opencu.bookit.adapter.out.persistence.entity.EventEntity;
import com.opencu.bookit.domain.model.event.EventModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class})
public interface EventMapper {

    @Mapping(target = "userModels", source = "users")
    EventModel toModel(EventEntity entity);

    @Mapping(target = "users", source = "userModels")
    EventEntity toEntity(EventModel model);

    List<EventModel> toModelList(List<EventEntity> entities);
}