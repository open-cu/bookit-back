package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.adapter.in.web.dto.request.NewsUpdateRequest;
import com.opencu.bookit.adapter.in.web.dto.response.NewsResponse;
import com.opencu.bookit.adapter.in.web.mapper.NewsResponseMapper;
import com.opencu.bookit.application.service.news.NewsService;
import com.opencu.bookit.application.service.photo.PhotoService;
import com.opencu.bookit.domain.model.contentcategory.ThemeTags;
import com.opencu.bookit.domain.model.news.NewsModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Tag(name = "admin-news-controller-v-1", description = "For admins only")
@RestController
@RequestMapping("/api/v1/news")
public class AdminNewsControllerV1 {

    private final NewsService newsService;
    private final PhotoService photoService;
    private final NewsResponseMapper newsResponseMapper;

    public AdminNewsControllerV1(NewsService newsService, PhotoService photoService, NewsResponseMapper newsResponseMapper) {
        this.newsService = newsService;
        this.photoService = photoService;
        this.newsResponseMapper = newsResponseMapper;
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(@securityService.getAdmin())")
    @Operation(summary = "Get all news with optional filters, search, pagination and sorting")
    @GetMapping
    public ResponseEntity<Page<NewsResponse>> getAllNews(
            @RequestParam(required = false) Set<ThemeTags> tags,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "true") Boolean sendPhotos,
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
                return newsResponseMapper.toResponse(model, sendPhotos);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(@securityService.getAdmin())")
    @Operation(summary = "Get news by id")
    @GetMapping("/{newsId}")
    public ResponseEntity<NewsResponse> getById(
            @RequestParam(defaultValue = "true") Boolean sendPhotos,
            @PathVariable UUID newsId
    ) {
        try {
            try {
                return ResponseEntity.ok(newsResponseMapper.toResponse(newsService.findById(newsId), sendPhotos));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(@securityService.getAdmin())")

    @Operation(summary = "Delete news from database")
    @DeleteMapping("/{newsId}")
    public ResponseEntity<?> deleteNews(
            @PathVariable UUID newsId
    ) {
        newsService.delete(newsId);
        return ResponseEntity.ok("News are successfully deleted");
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(@securityService.getAdmin())")
    @Operation(
            summary = "Update news by id. FOR ADMINS ONLY!",
            description = "Content-type: multipart/form-data, see Postman tests for more details."
    )
    @PutMapping("/{newsId}")
    public ResponseEntity<NewsResponse> updateNews(
            @RequestParam(defaultValue = "true") Boolean sendPhotos,
            @PathVariable UUID newsId,
            @RequestPart("newsUpdateRequest") NewsUpdateRequest newsUpdateRequest,
            @RequestPart("photos") List<MultipartFile> photos
    ) {
        try {
            List<String> keys = null;
            keys = photoService.upload(photos);
            NewsModel news = newsService.udpateNews(
                    newsId,
                    newsUpdateRequest.title(),
                    Optional.ofNullable(newsUpdateRequest.shortDescription()),
                    newsUpdateRequest.fullDescription(),
                    newsUpdateRequest.tags(),
                    keys
            );
                return ResponseEntity.ok(newsResponseMapper.toResponse(news, sendPhotos));

        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(@securityService.getAdmin())")
    @Operation(
            summary = "Create news. FOR ADMINS ONLY!",
            description = "Content-type: multipart/form-data, see Postman tests for more details."
    )
    @PostMapping
    public ResponseEntity<NewsResponse> createNews(
            @RequestParam(defaultValue = "true") Boolean sendPhotos,
            @RequestPart("newsUpdateRequest") NewsUpdateRequest newsUpdateRequest,
            @RequestPart("photos") List<MultipartFile> photos
    ) {
        List<String> keys = null;
        try {
            keys = photoService.upload(photos);
        NewsModel news = newsService.createNews(
                newsUpdateRequest.title(),
                Optional.ofNullable(newsUpdateRequest.shortDescription()),
                newsUpdateRequest.fullDescription(),
                newsUpdateRequest.tags(),
                keys
        );
            return ResponseEntity.status(HttpStatus.CREATED).body(newsResponseMapper.toResponse(news, sendPhotos));
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
