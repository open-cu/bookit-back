package com.opencu.bookit.adapter.out.persistence.mapper;

import com.opencu.bookit.adapter.out.persistence.entity.RoleEntity;
import com.opencu.bookit.domain.model.user.Role;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

    Role toModel(RoleEntity entity);

    RoleEntity toEntity(Role model);

    List<Role> toModelList(List<RoleEntity> entities);
}