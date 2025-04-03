package ru.tbank.bookit.book_it_backend.controller;

import org.springframework.data.util.Pair;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.bookit.book_it_backend.model.News;
import ru.tbank.bookit.book_it_backend.model.NewsTag;
import ru.tbank.bookit.book_it_backend.service.NewsService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/news")
public class NewsController {
    private final NewsService bookingService;

    public NewsController(NewsService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<News>> getAllNews() {
        List<News> news = bookingService.findAll();
        return ResponseEntity.ok(news);
    }

    @GetMapping("/by-tags")
    public ResponseEntity<List<News>> getAllNewsByTags(
            @PathVariable
            @RequestParam(required = true) Set<NewsTag> tags) {
        List<News> news = bookingService.findByTags(tags);
        return ResponseEntity.ok(news);
    }
}
