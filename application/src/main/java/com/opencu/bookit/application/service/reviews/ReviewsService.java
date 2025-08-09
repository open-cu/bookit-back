package com.opencu.bookit.application.service.reviews;

import com.opencu.bookit.application.port.out.reviews.DeleteReviewsPort;
import com.opencu.bookit.application.port.out.reviews.LoadReviewsPort;
import com.opencu.bookit.application.port.out.reviews.SaveReviewsPort;
import com.opencu.bookit.application.port.out.user.LoadUserPort;
import com.opencu.bookit.domain.model.area.Review;
import com.opencu.bookit.domain.model.reviews.ReviewsModel;
import com.opencu.bookit.domain.model.user.UserModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReviewsService {
    private final LoadReviewsPort loadReviewsPort;
    private final SaveReviewsPort saveReviewsPort;
    private final DeleteReviewsPort deleteReviewsPort;
    private final LoadUserPort loadUserPort;

    @Value("${booking.zone-id}")
    private ZoneId zoneId;

    public ReviewsService(LoadReviewsPort loadReviewsPort, SaveReviewsPort saveReviewsPort, DeleteReviewsPort deleteReviewsPort, LoadUserPort loadUserPort) {
        this.loadReviewsPort = loadReviewsPort;
        this.saveReviewsPort = saveReviewsPort;
        this.deleteReviewsPort = deleteReviewsPort;
        this.loadUserPort = loadUserPort;
    }

    public Page<ReviewsModel> findWithFilters(
            UUID userId,
            Byte rating,
            Pageable pageable
    ) {
        return loadReviewsPort.findWithFilters(userId, rating, pageable);
    }

    public ReviewsModel findById(UUID reviewId) {
        Optional<ReviewsModel> reviewsOpt = loadReviewsPort.findById(reviewId);
        if (reviewsOpt.isEmpty()) {
            throw new NoSuchElementException("No such review " + reviewId + " found");
        }
        return reviewsOpt.get();
    }

    public void deleteReview(UUID reviewId) {
        deleteReviewsPort.deleteById(reviewId);
    }

    public ReviewsModel createReview(
            UUID userId,
            int rating,
            String comment
    ) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        Review review = new Review();
        Optional<UserModel> userOpt = loadUserPort.findById(userId);
        if (userOpt.isEmpty()) {
            throw new NoSuchElementException("No such user " + userId + " found");
        }
        review.setUserModel(loadUserPort.findById(userId).get());
        review.setRating((byte) rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now(zoneId));
        return saveReviewsPort.save(review);
    }
}
