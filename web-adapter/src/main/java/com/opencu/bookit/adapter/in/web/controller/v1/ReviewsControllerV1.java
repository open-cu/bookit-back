package com.opencu.bookit.adapter.in.web.controller.v1;

import com.opencu.bookit.adapter.in.web.dto.response.ReviewsResponse;
import com.opencu.bookit.adapter.in.web.mapper.ReviewsResponseMapper;
import com.opencu.bookit.application.service.reviews.ReviewsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewsControllerV1 {
    private final ReviewsService reviewsService;
    private final ReviewsResponseMapper reviewsResponseMapper;

    public ReviewsControllerV1(ReviewsService reviewsService, ReviewsResponseMapper reviewsResponseMapper) {
        this.reviewsService = reviewsService;
        this.reviewsResponseMapper = reviewsResponseMapper;
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(SecurityService.getAdmin())")
    @GetMapping
    public ResponseEntity<Page<ReviewsResponse>> getReviews(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) byte rating,
            @RequestParam(defaultValue = "${pagination.default-page}") int page,
            @RequestParam(defaultValue = "${pagination.default-size}") int size
    ) {
        Sort.Direction direction = Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(direction, "rating"));

        return ResponseEntity.ok(
                reviewsService.findWithFilters(userId, rating, pageable)
                        .map(reviewsResponseMapper::toResponse)
        );
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(SecurityService.getAdmin())")
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewsResponse> getById(
            @PathVariable UUID reviewId
    ) {
        try {
            return ResponseEntity.ok(
                    reviewsResponseMapper.toResponse(reviewsService.findById(reviewId))
            );
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("@securityService.isDev() or " +
            "@securityService.hasRequiredRole(SecurityService.getAdmin())")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteById(
            @PathVariable UUID reviewId
    ) {
        reviewsService.deleteReview(reviewId);
        return ResponseEntity.ok("Review successfully deleted");
    }
}
