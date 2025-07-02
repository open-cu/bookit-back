package com.opencu.bookit.application.news.service;

import com.opencu.bookit.application.news.port.out.LoadNewsPort;
import com.opencu.bookit.application.news.port.out.SaveNewsPort;
import com.opencu.bookit.domain.model.event.ThemeTags;
import com.opencu.bookit.domain.model.news.News;
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

    public List<News> findAll() {
        return loadNewsPort.findAll();
    }

    @Transactional(readOnly = true)
    public List<News> findByTags(Set<ThemeTags> tags){
        return loadNewsPort.findByTags(tags);
    }
}
