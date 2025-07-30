package com.opencu.bookit.adapter.out.persistence.adapter;

import com.opencu.bookit.adapter.out.persistence.entity.ReviewEntity;
import com.opencu.bookit.adapter.out.persistence.entity.UserEntity;
import com.opencu.bookit.adapter.out.persistence.mapper.ReviewMapper;
import com.opencu.bookit.adapter.out.persistence.repository.ReviewRepository;
import com.opencu.bookit.adapter.out.persistence.repository.UserRepository;
import com.opencu.bookit.application.port.out.reviews.DeleteReviewsPort;
import com.opencu.bookit.application.port.out.reviews.LoadReviewsPort;
import com.opencu.bookit.application.port.out.reviews.SaveReviewsPort;
import com.opencu.bookit.domain.model.area.Review;
import com.opencu.bookit.domain.model.reviews.ReviewsModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReviewsPersistenceAdapter implements
        LoadReviewsPort, SaveReviewsPort, DeleteReviewsPort {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public Page<ReviewsModel> findWithFilters(UUID userId, Byte rating, Pageable pageable) {
        Specification<ReviewEntity> spec = Specification.where(null);

        if (rating != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("rating"), rating));
        }

        
        if (userId != null) {
            Optional<UserEntity> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                UserEntity user = userOpt.get();
                spec = spec.and((root, query, cb) ->
                        cb.equal(root.get("userEntity"), user));
            }
        }
        
        return reviewRepository.findAll(spec, pageable)
                .map(reviewMapper::toReviewsModel);
    }

    public Optional<ReviewsModel> findById(UUID reviewId) {
        return reviewRepository.findById(reviewId)
                .map(reviewMapper::toReviewsModel);
    }

    @Override
    public void deleteById(UUID reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    @Override
    public ReviewsModel save(Review model) {
        return reviewMapper.toReviewsModel(
                reviewRepository.save(reviewMapper.toEntity(model))
        );
    }
}
