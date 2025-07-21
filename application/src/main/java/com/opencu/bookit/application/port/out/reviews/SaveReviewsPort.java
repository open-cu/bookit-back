package com.opencu.bookit.application.port.out.reviews;

import com.opencu.bookit.domain.model.area.Review;
import com.opencu.bookit.domain.model.reviews.ReviewsModel;

public interface SaveReviewsPort {
    ReviewsModel save(Review model);
}
