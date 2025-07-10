package com.opencu.bookit.adapter.out.persistence.mapper;

import com.opencu.bookit.adapter.out.persistence.entity.HallOccupancyEntity;
import com.opencu.bookit.domain.model.statistics.HallOccupancyModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HallOccupancyMapper {

    HallOccupancyModel toModel(HallOccupancyEntity entity);

    HallOccupancyEntity toEntity(HallOccupancyModel model);

    List<HallOccupancyModel> toModelList(List<HallOccupancyEntity> entities);
}