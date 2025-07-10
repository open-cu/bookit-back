package com.opencu.bookit.adapter.out.persistence.mapper;

import com.opencu.bookit.adapter.out.persistence.entity.AreaEntity;
import com.opencu.bookit.domain.model.area.AreaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class AreaMapper {
    @Autowired
    @Lazy
    protected BookingMapper bookingMapper;

    @Autowired
    @Lazy
    protected TicketMapper ticketMapper;

    @Mapping(target = "bookingModels", source = "bookingEntities")
    @Mapping(target = "ticketModels", source = "ticketEntities")
    public abstract AreaModel toModel(AreaEntity entity);

    @Mapping(target = "bookingEntities", source = "bookingModels")
    @Mapping(target = "ticketEntities", source = "ticketModels")
    public abstract AreaEntity toEntity(AreaModel model);

    public abstract List<AreaModel> toModelList(List<AreaEntity> entities);
}