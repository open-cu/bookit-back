package com.opencu.bookit.adapter.in.web.controller;

import com.opencu.bookit.application.service.news.NewsService;
import com.opencu.bookit.domain.model.event.ThemeTags;
import com.opencu.bookit.domain.model.news.NewsModel;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/news")
public class NewsController {
    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @Operation(description = "Returns information in the list format about all news")
    @GetMapping()
    public ResponseEntity<List<NewsModel>> getAllNews() {
        List<NewsModel> news = newsService.findAll();
        return ResponseEntity.ok(news);
    }

    @Operation(description = "Returns information in list format about all news for a specific tag")
    @GetMapping("/by-tags")
    public ResponseEntity<List<NewsModel>> getAllNewsByTags(
            @RequestParam Set<ThemeTags> tags) {
        List<NewsModel> news = newsService.findByTags(tags);
        return ResponseEntity.ok(news);
    }
}
