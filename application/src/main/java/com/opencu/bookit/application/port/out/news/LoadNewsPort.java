package com.opencu.bookit.application.port.out.news;

import com.opencu.bookit.domain.model.contentcategory.ThemeTags;
import com.opencu.bookit.domain.model.news.NewsModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface LoadNewsPort {
    List<NewsModel> findByTags(Set<ThemeTags> tags);
    List<NewsModel> findAll();
    Page<NewsModel> findWithFilters(Set<ThemeTags> tags, String search, Pageable pageable);
    Optional<NewsModel> findById(UUID newsId);
}
