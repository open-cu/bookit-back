package com.opencu.bookit.adapter.in.web.mapper;

import com.opencu.bookit.adapter.in.web.dto.response.NewsResponse;
import com.opencu.bookit.domain.model.news.NewsModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface NewsResponseMapper {
    NewsResponse toResponse(NewsModel model);
}
