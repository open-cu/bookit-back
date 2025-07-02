package com.opencu.bookit.application.news.port.out;

import com.opencu.bookit.domain.model.event.ThemeTags;
import com.opencu.bookit.domain.model.news.News;

import java.util.List;
import java.util.Set;

public interface LoadNewsPort {
    List<News> findByTags(Set<ThemeTags> tags);
    List<News> findAll();
}
