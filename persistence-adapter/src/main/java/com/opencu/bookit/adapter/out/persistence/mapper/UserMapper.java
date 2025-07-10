package com.opencu.bookit.adapter.out.persistence.mapper;

import com.opencu.bookit.adapter.out.persistence.entity.UserEntity;
import com.opencu.bookit.domain.model.user.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {RoleMapper.class}
)
public abstract class UserMapper {

    @Autowired
    @Lazy
    protected ReviewMapper reviewMapper;

    @Mapping(target = "roleModels", source = "roleEntities")
    @Mapping(target = "reviews", source = "reviewEntities")
    public abstract UserModel toModel(UserEntity entity);

    @Mapping(target = "roleEntities", source = "roleModels")
    @Mapping(target = "reviewEntities", source = "reviews")
    public abstract UserEntity toEntity(UserModel model);

    public abstract List<UserModel> toModelList(List<UserEntity> entities);
}