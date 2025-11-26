package com.opencu.bookit.application.service.reviews;

import com.opencu.bookit.application.port.out.reviews.DeleteReviewsPort;
import com.opencu.bookit.application.port.out.reviews.LoadReviewsPort;
import com.opencu.bookit.application.port.out.reviews.SaveReviewsPort;
import com.opencu.bookit.application.port.out.user.LoadUserPort;
import com.opencu.bookit.domain.model.area.Review;
import com.opencu.bookit.domain.model.reviews.ReviewsModel;
import com.opencu.bookit.domain.model.user.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewsServiceTest {

    @Mock private LoadReviewsPort loadReviewsPort;
    @Mock private SaveReviewsPort saveReviewsPort;
    @Mock private DeleteReviewsPort deleteReviewsPort;
    @Mock private LoadUserPort loadUserPort;

    @InjectMocks private ReviewsService service;

    private UUID reviewId;
    private UUID userId;
    private ReviewsModel existing;
    private UserModel user;

    @BeforeEach
    void setUp() {
        reviewId = UUID.randomUUID();
        userId = UUID.randomUUID();
        existing = new ReviewsModel();
        existing.setId(reviewId);
        existing.setUserId(userId);
        existing.setRating((byte) 4);
        existing.setComment("Nice place");
        existing.setCreatedAt(LocalDateTime.of(2030, 1, 1, 12, 0));

        user = new UserModel();
        user.setId(userId);
        user.setEmail("user@example.com");

        ReflectionTestUtils.setField(service, "zoneId", ZoneId.of("UTC"));
    }

    @Test
    @DisplayName("findWithFilters delegates to LoadReviewsPort")
    void findWithFilters_ok() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<ReviewsModel> page = new PageImpl<>(List.of(existing), pageable, 1);
        when(loadReviewsPort.findWithFilters(userId, (byte)4, pageable)).thenReturn(page);

        Page<ReviewsModel> res = service.findWithFilters(userId, (byte)4, pageable);
        assertEquals(1, res.getTotalElements());
        assertEquals(reviewId, res.getContent().get(0).getId());
        verify(loadReviewsPort).findWithFilters(userId, (byte)4, pageable);
        verifyNoMoreInteractions(loadReviewsPort);
    }

    @Test
    @DisplayName("findById returns existing review")
    void findById_found() {
        when(loadReviewsPort.findById(reviewId)).thenReturn(Optional.of(existing));
        ReviewsModel res = service.findById(reviewId);
        assertEquals(reviewId, res.getId());
        verify(loadReviewsPort).findById(reviewId);
    }

    @Test
    @DisplayName("findById throws when missing")
    void findById_missing() {
        UUID missing = UUID.randomUUID();
        when(loadReviewsPort.findById(missing)).thenReturn(Optional.empty());
        NoSuchElementException ex = assertThrows(NoSuchElementException.class, () -> service.findById(missing));
        assertTrue(ex.getMessage().contains(missing.toString()));
        verify(loadReviewsPort).findById(missing);
    }

    @Test
    @DisplayName("deleteReview delegates to DeleteReviewsPort")
    void deleteReview_ok() {
        service.deleteReview(reviewId);
        verify(deleteReviewsPort).deleteById(reviewId);
        verifyNoMoreInteractions(deleteReviewsPort);
    }

    @Test
    @DisplayName("createReview saves with valid rating and existing user")
    void createReview_ok() {
        when(loadUserPort.findById(userId)).thenReturn(Optional.of(user));
        when(saveReviewsPort.save(any(Review.class))).thenAnswer(invocation -> {
            Review r = invocation.getArgument(0);
            ReviewsModel model = new ReviewsModel();
            model.setId(UUID.randomUUID());
            model.setUserId(r.getUserModel().getId());
            model.setRating(r.getRating());
            model.setComment(r.getComment());
            model.setCreatedAt(r.getCreatedAt());
            return model;
        });

        LocalDateTime before = LocalDateTime.now(ZoneId.of("UTC")).minusSeconds(1);
        ReviewsModel saved = service.createReview(userId, 5, "Great!");
        LocalDateTime after = LocalDateTime.now(ZoneId.of("UTC")).plusSeconds(1);

        assertEquals(userId, saved.getUserId());
        assertEquals((byte)5, saved.getRating());
        assertEquals("Great!", saved.getComment());
        assertNotNull(saved.getCreatedAt());
        assertTrue(!saved.getCreatedAt().isBefore(before) && !saved.getCreatedAt().isAfter(after));

        verify(loadUserPort).findById(userId);
        verify(saveReviewsPort).save(any(Review.class));
    }

    @Test
    @DisplayName("createReview throws on invalid rating")
    void createReview_invalidRating() {
        assertThrows(IllegalArgumentException.class, () -> service.createReview(userId, 0, "bad"));
        assertThrows(IllegalArgumentException.class, () -> service.createReview(userId, 6, "bad"));
        verifyNoInteractions(loadUserPort, saveReviewsPort);
    }

    @Test
    @DisplayName("createReview throws when user missing")
    void createReview_userMissing() {
        when(loadUserPort.findById(userId)).thenReturn(Optional.empty());
        NoSuchElementException ex = assertThrows(NoSuchElementException.class, () -> service.createReview(userId, 3, "ok"));
        assertTrue(ex.getMessage().contains(userId.toString()));
        verify(loadUserPort).findById(userId);
        verifyNoInteractions(saveReviewsPort);
    }
}

