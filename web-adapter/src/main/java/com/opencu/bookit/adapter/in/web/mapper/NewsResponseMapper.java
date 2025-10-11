package com.opencu.bookit.adapter.in.web.mapper;

import com.opencu.bookit.adapter.in.web.dto.response.EventResponse;
import com.opencu.bookit.adapter.in.web.dto.response.NewsResponse;
import com.opencu.bookit.application.service.photo.PhotoService;
import com.opencu.bookit.domain.model.event.EventModel;
import com.opencu.bookit.domain.model.news.NewsModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class NewsResponseMapper {
    private final PhotoService service;

    public NewsResponseMapper(PhotoService service) {
        this.service = service;
    }

    public NewsResponse toResponse(NewsModel model, Boolean sendPhotos) throws IOException {
        return new NewsResponse(
            model.getId(),
            model.getTitle(),
            model.getShortDescription().get(),
            model.getFullDescription(),
            model.getTags(),
            service.getImagesFromKeys(model.getKeys(), sendPhotos),
            model.getCreatedAt()
        );
    }

    public List<NewsResponse> toNewsResponseList(List<NewsModel> newsModels, Boolean sendPhotos) throws IOException {
        List<NewsResponse> newsResponseList = new ArrayList<>();
        for (var news:  newsModels) {
            newsResponseList.add(toResponse(news, sendPhotos));
        }
        return newsResponseList;
    }
}
