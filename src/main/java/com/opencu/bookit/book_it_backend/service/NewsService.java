package ru.tbank.bookit.book_it_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.bookit.book_it_backend.model.News;
import ru.tbank.bookit.book_it_backend.model.ThemeTags;
import ru.tbank.bookit.book_it_backend.repository.NewsRepository;

import java.util.List;
import java.util.Set;

@Service
public class NewsService {
    private final NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public List<News> findAll() {
        return (List<News>) newsRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<News> findByTags(Set<ThemeTags> tags){
        return newsRepository.findByTagsIn(tags);
    }
}
