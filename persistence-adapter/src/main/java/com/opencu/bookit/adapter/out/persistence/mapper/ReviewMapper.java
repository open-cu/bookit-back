package com.opencu.bookit.adapter.out.persistence.mapper;

import com.opencu.bookit.adapter.out.persistence.entity.ReviewEntity;
import com.opencu.bookit.domain.model.area.Review;
import com.opencu.bookit.domain.model.reviews.ReviewsModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ReviewMapper {

    @Autowired
    @Lazy
    protected UserMapper userMapper;

    @Mapping(target = "userModel", source = "userEntity")
    public abstract Review toModel(ReviewEntity entity);

    @Mapping(target = "userEntity", source = "userModel")
    public abstract ReviewEntity toEntity(Review model);

    @Mapping(target = "userId", expression = "java(userEntity.getId())")
    public abstract ReviewsModel toReviewsModel(ReviewEntity review);

    public abstract List<Review> toModelList(List<ReviewEntity> entities);
}