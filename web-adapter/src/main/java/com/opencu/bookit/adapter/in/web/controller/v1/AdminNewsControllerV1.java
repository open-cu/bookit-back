package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.adapter.in.web.dto.request.NewsUpdateRequest;
import com.opencu.bookit.adapter.in.web.dto.response.NewsResponse;
import com.opencu.bookit.adapter.in.web.mapper.NewsResponseMapper;
import com.opencu.bookit.application.service.news.NewsService;
import com.opencu.bookit.domain.model.contentcategory.ThemeTags;
import com.opencu.bookit.domain.model.news.NewsModel;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/news")
public class AdminNewsControllerV1 {

    private final NewsService newsService;
    private final NewsResponseMapper newsResponseMapper;

    public AdminNewsControllerV1(NewsService newsService, NewsResponseMapper newsResponseMapper) {
        this.newsService = newsService;
        this.newsResponseMapper = newsResponseMapper;
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(SecurityService.getAdmin())")
    @Operation(summary = "Get all public news with optional filters, search, pagination and sorting")
    @GetMapping
    public ResponseEntity<Page<NewsResponse>> getAllPublicNews(
            @RequestParam(required = false) Set<ThemeTags> tags,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "${pagination.default-page}") int page,
            @RequestParam(defaultValue = "${pagination.default-size}") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        String[] sortParams = sort.split(",", 2);
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortBy = sortParams[0];
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<NewsModel> newsPage = newsService.findWithFilters(tags, search, pageable);
        return ResponseEntity.ok(newsPage.map(model -> {
            try {
                return newsResponseMapper.toResponse(model);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(SecurityService.getAdmin())")
    @GetMapping("/{newsId}")
    public ResponseEntity<NewsResponse> getById(
            @PathVariable UUID newsId
    ) {
        try {
            try {
                return ResponseEntity.ok(newsResponseMapper.toResponse(newsService.findById(newsId)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(SecurityService.getAdmin())")
    @DeleteMapping("/{newsId}")
    public ResponseEntity<?> deleteNews(
            @PathVariable UUID newsId
    ) {
        newsService.delete(newsId);
        return ResponseEntity.ok("News are successfully deleted");
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(SecurityService.getAdmin())")
    @PutMapping("/{newsId}")
    public ResponseEntity<NewsResponse> updateNews(
            @PathVariable UUID newsId,
            @RequestBody NewsUpdateRequest newsUpdateRequest
    ) {
        try {
            NewsModel news = newsService.udpateNews(
                    newsId,
                    newsUpdateRequest.title(),
                    newsUpdateRequest.description(),
                    newsUpdateRequest.tags(),
                    newsUpdateRequest.keys()
            );
            try {
                return ResponseEntity.ok(newsResponseMapper.toResponse(news));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(SecurityService.getAdmin())")
    @PostMapping
    public ResponseEntity<NewsResponse> createNews(
            @RequestBody NewsUpdateRequest newsUpdateRequest
    ) {
        NewsModel news = newsService.createNews(
                newsUpdateRequest.title(),
                newsUpdateRequest.description(),
                newsUpdateRequest.tags(),
                newsUpdateRequest.keys()
        );
        try {
            return ResponseEntity.ok(newsResponseMapper.toResponse(news));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
