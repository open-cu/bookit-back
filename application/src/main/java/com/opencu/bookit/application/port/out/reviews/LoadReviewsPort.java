package com.opencu.bookit.application.port.out.reviews;

import com.opencu.bookit.domain.model.reviews.ReviewsModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface LoadReviewsPort {
    Page<ReviewsModel> findWithFilters(UUID userId, Byte rating, Pageable pageable);
    Optional<ReviewsModel> findById(UUID reviewId);
}
