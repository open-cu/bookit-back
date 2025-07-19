package com.opencu.bookit.adapter.in.web.mapper;

import com.opencu.bookit.adapter.in.web.dto.response.ReviewsResponse;
import com.opencu.bookit.domain.model.reviews.ReviewsModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ReviewsResponseMapper {
    ReviewsResponse toResponse(ReviewsModel reviewsModel);
}
