package ru.tbank.bookit.book_it_backend.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.bookit.book_it_backend.DTO.CreateNewsRequest;
import ru.tbank.bookit.book_it_backend.model.News;
import ru.tbank.bookit.book_it_backend.model.ThemeTags;
import ru.tbank.bookit.book_it_backend.repository.NewsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@Service
public class NewsService {
    private final NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public List<News> findAll() {
        return (List<News>) newsRepository.findAll();
    }

    public List<News> findByTags(Set<ThemeTags> tags){
        return newsRepository.findByTagsIn(tags);
    }

    @Transactional
    public News createNews(CreateNewsRequest request) {
        News news = new News();
        news.setTitle(request.getTitle());
        news.setDescription(request.getDescription());
        news.setTags(request.getTags());
        news.setCreatedAt(LocalDateTime.now());

        return newsRepository.save(news);
    }

    @Transactional
    public News updateNews(UUID newsId, String title, String description, Set<ThemeTags> tags) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new EntityNotFoundException("News not found with id: " + newsId));

        updateIfChanged(news::setTitle, news.getTitle(), title);
        updateIfChanged(news::setDescription, news.getDescription(), description);
        updateIfChanged(news::setTags, news.getTags(), tags);

        return newsRepository.save(news);
    }

    @Transactional
    public void deleteNews(UUID newsId) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new EntityNotFoundException("News not found with id: " + newsId));

        newsRepository.delete(news);
    }

    private <T> void updateIfChanged(Consumer<T> setter, T oldValue, T newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            setter.accept(newValue);
        }
    }
}
