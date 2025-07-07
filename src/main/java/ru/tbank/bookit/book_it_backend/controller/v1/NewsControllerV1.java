package ru.tbank.bookit.book_it_backend.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.bookit.book_it_backend.model.News;
import ru.tbank.bookit.book_it_backend.model.ThemeTags;
import ru.tbank.bookit.book_it_backend.service.NewsService;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/public/news")
public class NewsControllerV1 {
    private final NewsService newsService;

    public NewsControllerV1(NewsService newsService) {
        this.newsService = newsService;
    }

    @Operation(summary = "Get all public news with optional filters, search, pagination and sorting")
    @GetMapping
    public ResponseEntity<Page<News>> getAllPublicNews(
            @RequestParam(required = false) Set<ThemeTags> tags,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        String[] sortParams = sort.split(",", 2);
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortBy = sortParams[0];
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        var spec = newsService.buildSpecification(tags, search);
        Page<News> newsPage = newsService.findWithFilters(tags, search, pageable, spec);
        return ResponseEntity.ok(newsPage);
    }
}