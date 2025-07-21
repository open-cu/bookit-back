package com.opencu.bookit.domain.model.reviews;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewsModel {
    private UUID id;
    private UUID userId;
    private byte rating;
    private String comment;
    private LocalDateTime createdAt;
}
