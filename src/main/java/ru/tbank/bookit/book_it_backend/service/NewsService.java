package ru.tbank.bookit.book_it_backend.service;

import org.springframework.stereotype.Service;
import ru.tbank.bookit.book_it_backend.model.Booking;
import ru.tbank.bookit.book_it_backend.model.News;
import ru.tbank.bookit.book_it_backend.model.NewsTag;
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

    public List<News> findByTags(Set<NewsTag> tags){
        return newsRepository.findByTagsIn(tags);
    }

}
