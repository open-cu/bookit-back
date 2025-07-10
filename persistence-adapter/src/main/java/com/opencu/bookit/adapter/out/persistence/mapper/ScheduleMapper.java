package com.opencu.bookit.adapter.out.persistence.mapper;

import com.opencu.bookit.adapter.out.persistence.entity.ScheduleEntity;
import com.opencu.bookit.domain.model.schedule.ScheduleModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ScheduleMapper {

    ScheduleModel toModel(ScheduleEntity entity);

    ScheduleEntity toEntity(ScheduleModel model);

    List<ScheduleModel> toModelList(List<ScheduleEntity> entities);
}