package com.opencu.bookit.adapter.out.persistence.adapter;

import com.opencu.bookit.adapter.out.persistence.entity.NewsEntity;
import com.opencu.bookit.adapter.out.persistence.mapper.NewsMapper;
import com.opencu.bookit.adapter.out.persistence.repository.NewsRepository;
import com.opencu.bookit.application.port.out.news.DeleteNewsPort;
import com.opencu.bookit.application.port.out.news.LoadNewsPort;
import com.opencu.bookit.application.port.out.news.SaveNewsPort;
import com.opencu.bookit.domain.model.contentcategory.ThemeTags;
import com.opencu.bookit.domain.model.news.NewsModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NewsPersistenceAdapter implements LoadNewsPort, SaveNewsPort,
        DeleteNewsPort {

    private final NewsRepository newsRepository;
    private final NewsMapper newsMapper;

    @Override
    public List<NewsModel> findByTags(Set<ThemeTags> tags) {
        return newsMapper.toModelList(newsRepository.findByTagsIn(tags));
    }

    @Override
    public List<NewsModel> findAll() {
        return newsMapper.toModelList((List<NewsEntity>) newsRepository.findAll());
    }
    @Override
    public Page<NewsModel> findWithFilters(Set<ThemeTags> tags, String search, Pageable pageable) {
        var spec = buildSpecification(tags, search);
        return newsRepository.findAll(spec, pageable).map(newsMapper::toModel);
    }

    public Specification<NewsEntity> buildSpecification(Set<ThemeTags> tags, String search) {
        Specification<NewsEntity> spec = Specification.where(null);
        if (tags != null && !tags.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.join("tags").in(tags));
        }
        if (search != null && !search.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("title")), "%" + search.toLowerCase() + "%"),
                            cb.like(root.get("description"), "%" + search + "%")
                         )
           );
        }
        return spec;
    }

    @Override
    public NewsModel save(NewsModel newsModel) {
        var entity = newsMapper.toEntity(newsModel);
        var savedEntity = newsRepository.save(entity);
        return newsMapper.toModel(savedEntity);
    }

    public Optional<NewsModel> findById(UUID newsId) {
        return newsRepository.findById(newsId)
                .map(newsMapper::toModel);
    }

    @Override
    public void delete(UUID newsId) {
        newsRepository.deleteById(newsId);
    }
}

