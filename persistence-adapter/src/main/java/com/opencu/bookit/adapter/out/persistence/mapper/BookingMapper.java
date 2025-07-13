package com.opencu.bookit.adapter.out.persistence.mapper;

import com.opencu.bookit.adapter.out.persistence.entity.BookingEntity;
import com.opencu.bookit.domain.model.booking.BookingModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class})
public abstract class BookingMapper {

    @Autowired
    @Lazy
    protected AreaMapper areaMapper;

    @Mapping(target = "userModel", source = "userEntity")
    @Mapping(target = "areaModel", source = "areaEntity")
    public abstract BookingModel toModel(BookingEntity entity);

    @Mapping(target = "userEntity", source = "userModel")
    @Mapping(target = "areaEntity", source = "areaModel")
    public abstract BookingEntity toEntity(BookingModel model);

    public abstract List<BookingModel> toModelList(List<BookingEntity> entities);

    public abstract List<BookingEntity> toEntityList(Set<BookingModel> models);
}