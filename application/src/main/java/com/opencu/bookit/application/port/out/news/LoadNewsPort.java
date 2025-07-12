package com.opencu.bookit.application.port.out.news;

import com.opencu.bookit.domain.model.event.ThemeTags;
import com.opencu.bookit.domain.model.news.NewsModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Set;

public interface LoadNewsPort {
    List<NewsModel> findByTags(Set<ThemeTags> tags);
    List<NewsModel> findAll();
    Page<NewsModel> findWithFilters(Set<ThemeTags> tags, String search, Pageable pageable);
}
