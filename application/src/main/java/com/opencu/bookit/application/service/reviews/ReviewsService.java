package com.opencu.bookit.application.service.reviews;

import com.opencu.bookit.application.port.out.reviews.DeleteReviewsPort;
import com.opencu.bookit.application.port.out.reviews.LoadReviewsPort;
import com.opencu.bookit.application.port.out.reviews.SaveReviewsPort;
import com.opencu.bookit.domain.model.reviews.ReviewsModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReviewsService {
    private final LoadReviewsPort loadReviewsPort;
    private final SaveReviewsPort saveReviewsPort;
    private final DeleteReviewsPort deleteReviewsPort;

    public ReviewsService(LoadReviewsPort loadReviewsPort, SaveReviewsPort saveReviewsPort, DeleteReviewsPort deleteReviewsPort) {
        this.loadReviewsPort = loadReviewsPort;
        this.saveReviewsPort = saveReviewsPort;
        this.deleteReviewsPort = deleteReviewsPort;
    }

    public Page<ReviewsModel> findWithFilters(
            UUID userId,
            byte rating,
            Pageable pageable
    ) {
        return loadReviewsPort.findWithFilters(userId, rating, pageable);
    }

    public ReviewsModel findById(UUID reviewId) {
        Optional<ReviewsModel> reviewsOpt = loadReviewsPort.findById(reviewId);
        if (reviewsOpt.isEmpty()) {
            throw new NoSuchElementException("No such review found");
        }
        return reviewsOpt.get();
    }

    public void deleteReview(UUID reviewId) {
        deleteReviewsPort.delete(reviewId);
    }
}
