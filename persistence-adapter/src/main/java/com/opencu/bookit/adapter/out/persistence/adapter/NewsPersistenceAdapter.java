package com.opencu.bookit.adapter.out.persistence.adapter;

import com.opencu.bookit.adapter.out.persistence.mapper.NewsMapper;
import com.opencu.bookit.adapter.out.persistence.repository.NewsRepository;
import com.opencu.bookit.application.port.out.news.LoadNewsPort;
import com.opencu.bookit.application.port.out.news.SaveNewsPort;
import com.opencu.bookit.domain.model.event.ThemeTags;
import com.opencu.bookit.domain.model.news.NewsModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class NewsPersistenceAdapter implements LoadNewsPort, SaveNewsPort {

    private final NewsRepository newsRepository;
    private final NewsMapper newsMapper;

    @Override
    public List<NewsModel> findByTags(Set<ThemeTags> tags) {
        return newsMapper.toModelList(newsRepository.findByTagsIn(tags));
    }

    @Override
    public List<NewsModel> findAll() {
        return newsMapper.toModelList(newsRepository.findAllByOrderByCreatedAtDesc());
    }

    @Override
    public NewsModel save(NewsModel newsModel) {
        var entity = newsMapper.toEntity(newsModel);
        var savedEntity = newsRepository.save(entity);
        return newsMapper.toModel(savedEntity);
    }
}

