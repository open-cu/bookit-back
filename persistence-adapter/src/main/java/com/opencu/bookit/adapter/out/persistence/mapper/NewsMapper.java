package com.opencu.bookit.adapter.out.persistence.mapper;

import com.opencu.bookit.adapter.out.persistence.entity.NewsEntity;
import com.opencu.bookit.domain.model.news.NewsModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NewsMapper {


    @Mapping(target = "fullDescription", source = "full_description")
    @Mapping(target = "shortDescription", source = "short_description")
    NewsModel toModel(NewsEntity entity);


    @Mapping(target = "full_description", source = "fullDescription")
    @Mapping(target = "short_description", source = "shortDescription")
    NewsEntity toEntity(NewsModel model);

    List<NewsModel> toModelList(List<NewsEntity> entities);
}