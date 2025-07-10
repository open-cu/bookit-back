package com.opencu.bookit.adapter.out.persistence.mapper;

import com.opencu.bookit.adapter.out.persistence.entity.RoleEntity;
import com.opencu.bookit.domain.model.user.RoleModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

    RoleModel toModel(RoleEntity entity);

    RoleEntity toEntity(RoleModel model);

    List<RoleModel> toModelList(List<RoleEntity> entities);
}