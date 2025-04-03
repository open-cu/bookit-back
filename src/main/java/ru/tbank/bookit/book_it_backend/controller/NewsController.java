package ru.tbank.bookit.book_it_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.bookit.book_it_backend.model.News;
import ru.tbank.bookit.book_it_backend.model.NewsTag;
import ru.tbank.bookit.book_it_backend.service.NewsService;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/news")
public class NewsController {
    private final NewsService newsService;

    public NewsController(NewsService bookingService) {
        this.newsService = bookingService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<News>> getAllNews() {
        List<News> news = newsService.findAll();
        return ResponseEntity.ok(news);
    }

    @GetMapping("/by-tags")
    public ResponseEntity<List<News>> getAllNewsByTags(
            @PathVariable
            @RequestParam(required = true) Set<NewsTag> tags) {
        List<News> news = newsService.findByTags(tags);
        return ResponseEntity.ok(news);
    }
}
