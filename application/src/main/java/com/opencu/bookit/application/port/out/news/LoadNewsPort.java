package com.opencu.bookit.application.port.out.news;

import com.opencu.bookit.domain.model.event.ThemeTags;
import com.opencu.bookit.domain.model.news.NewsModel;

import java.util.List;
import java.util.Set;

public interface LoadNewsPort {
    List<NewsModel> findByTags(Set<ThemeTags> tags);
    List<NewsModel> findAll();
}
