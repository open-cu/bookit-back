package com.opencu.bookit.application.port.out.reviews;

import java.util.UUID;

public interface DeleteReviewsPort {
    void deleteById(UUID reviewId);
}
