package com.opencu.bookit.adapter.out.persistence.mapper;

import com.opencu.bookit.adapter.out.persistence.entity.TicketEntity;
import com.opencu.bookit.domain.model.ticket.TicketModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class, AreaMapper.class}
)
public interface TicketMapper {

    @Mapping(target = "userModel", source = "userEntity")
    @Mapping(target = "areaModel", source = "areaEntity")
    TicketModel toModel(TicketEntity entity);

    @Mapping(target = "userEntity", source = "userModel")
    @Mapping(target = "areaEntity", source = "areaModel")
    TicketEntity toEntity(TicketModel model);

    List<TicketModel> toModelList(List<TicketEntity> entities);
}