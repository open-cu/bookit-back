package com.opencu.bookit.application.service.news;

import com.opencu.bookit.application.port.out.news.LoadNewsPort;
import com.opencu.bookit.application.port.out.news.SaveNewsPort;
import com.opencu.bookit.domain.model.event.ThemeTags;
import com.opencu.bookit.domain.model.news.NewsModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class NewsService {
    private final LoadNewsPort loadNewsPort;
    private final SaveNewsPort saveNewsPort;

    public NewsService(LoadNewsPort loadNewsPort, SaveNewsPort saveNewsPort) {
        this.loadNewsPort = loadNewsPort;
        this.saveNewsPort = saveNewsPort;
    }

    public List<NewsModel> findAll() {
        return loadNewsPort.findAll();
    }

    @Transactional(readOnly = true)
    public List<NewsModel> findByTags(Set<ThemeTags> tags){
        return loadNewsPort.findByTags(tags);
    }

    public Page<NewsModel> findWithFilters(Set<ThemeTags> tags, String search, Pageable pageable) {
        return loadNewsPort.findWithFilters(tags, search, pageable);
    }
}
