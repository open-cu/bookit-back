package com.opencu.bookit.application.service.news;

import com.opencu.bookit.application.port.out.news.DeleteNewsPort;
import com.opencu.bookit.application.port.out.news.LoadNewsPort;
import com.opencu.bookit.application.port.out.news.SaveNewsPort;
import com.opencu.bookit.domain.model.contentcategory.ThemeTags;
import com.opencu.bookit.domain.model.news.NewsModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class NewsService {
    private final LoadNewsPort loadNewsPort;
    private final SaveNewsPort saveNewsPort;
    private final DeleteNewsPort deleteNewsPort;

    @Value("${booking.zone-id}")
    private ZoneId zoneId;

    public NewsService(LoadNewsPort loadNewsPort, SaveNewsPort saveNewsPort, DeleteNewsPort deleteNewsPort) {
        this.loadNewsPort = loadNewsPort;
        this.saveNewsPort = saveNewsPort;
        this.deleteNewsPort = deleteNewsPort;
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

    public NewsModel findById(UUID newsId) {
        Optional<NewsModel> newsOpt = loadNewsPort.findById(newsId);
        if (newsOpt.isEmpty()) {
            throw new NoSuchElementException("No such news with id " + newsId + " found");
        }
        return newsOpt.get();
    }

    public void delete(UUID newsId) {
        deleteNewsPort.delete(newsId);
    }

    public NewsModel udpateNews(
            UUID newsId,
            String title,
            String description,
            List<ThemeTags> tags,
            List<String> keys
    ) {
        Optional<NewsModel> newsOpt = loadNewsPort.findById(newsId);
        if (newsOpt.isEmpty()) {
            throw  new NoSuchElementException("No such news with id " + newsId + " found");
        }
        NewsModel news = newsOpt.get();
        news.setTitle(title);
        news.setDescription(description);
        news.setTags(new HashSet<>(tags));
        news.setKeys(new ArrayList<>(keys));
        return saveNewsPort.save(news);
    }

    @Transactional
    public NewsModel createNews(
            String title,
            String description,
            List<ThemeTags> tags,
            List<String> keys
    ) {
        NewsModel newsModel = new NewsModel();
        newsModel.setTitle(title);
        newsModel.setDescription(description);
        newsModel.setTags(new HashSet<>(tags));
        newsModel.setCreatedAt(LocalDateTime.now(zoneId));
        newsModel.setKeys(keys);
        return saveNewsPort.save(newsModel);
    }
}
