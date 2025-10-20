package com.opencu.bookit.adapter.out.persistence.specifications;

import com.opencu.bookit.adapter.out.persistence.entity.EventEntity;
import com.opencu.bookit.adapter.out.persistence.entity.UserEntity;
import com.opencu.bookit.domain.model.contentcategory.*;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public final class EventSpecifications {

    private EventSpecifications() {}

    public static Specification<EventEntity> startBetweenInclusive(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) return null;
        return (root, query, cb) -> {
            Path<LocalDateTime> path = root.get("startTime");
            LocalDateTime from = startDate != null ? startDate.atStartOfDay() : LocalDate.MIN.atStartOfDay();
            LocalDateTime toExclusive = (endDate != null ? endDate.plusDays(1) : LocalDate.MAX).atStartOfDay();
            return cb.and(cb.greaterThanOrEqualTo(path, from), cb.lessThan(path, toExclusive));
        };
    }

    public static Specification<EventEntity> search(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String pattern = "%" + raw.trim().toLowerCase() + "%";
        return (root, query, cb) -> {
            Expression<String> name = cb.lower(root.get("name"));
            Expression<String> shortDesc = cb.lower(root.get("short_description"));
            Expression<String> fullDesc = cb.lower(root.get("full_description"));
            return cb.or(cb.like(name, pattern), cb.like(shortDesc, pattern), cb.like(fullDesc, pattern));
        };
    }

    public static Specification<EventEntity> hasAnyTags(Set<ThemeTags> tags) {
        return existsInCollection("tags", tags);
    }

    public static Specification<EventEntity> hasAnyFormats(Set<ContentFormat> formats) {
        return existsInCollection("formats", formats);
    }

    public static Specification<EventEntity> hasAnyTimes(Set<ContentTime> times) {
        return existsInCollection("times", times);
    }

    public static Specification<EventEntity> hasAnyParticipationFormats(Set<ParticipationFormat> participationFormats) {
        return existsInCollection("participationFormats", participationFormats);
    }

    public static Specification<EventEntity> hasAnyTargetAudiences(Set<TargetAudience> targetAudiences) {
        return existsInCollection("targetAudiences", targetAudiences);
    }

    private static <A> Specification<EventEntity> existsInCollection(String attributeName, Set<A> values) {
        if (values == null || values.isEmpty()) return null;
        return (root, query, cb) -> {
            Subquery<Integer> sub = query.subquery(Integer.class);
            Root<EventEntity> subRoot = sub.from(EventEntity.class);
            Join<EventEntity, A> join = subRoot.join(attributeName);
            sub.select(cb.literal(1));
            Predicate sameEvent = cb.equal(subRoot, root);
            Predicate inValues = join.in(values);
            sub.where(cb.and(sameEvent, inValues));
            return cb.exists(sub);
        };
    }

    public static Specification<EventEntity> registeredBy(UUID userId) {
        if (userId == null) return null;
        return (root, query, cb) -> {
            Subquery<Integer> sub = query.subquery(Integer.class);
            Root<EventEntity> e2 = sub.from(EventEntity.class);
            Join<EventEntity, UserEntity> u = e2.join("users");
            sub.select(cb.literal(1));
            sub.where(cb.equal(e2, root), cb.equal(u.get("id"), userId));
            return cb.exists(sub);
        };
    }

    public static Specification<EventEntity> availableFor(UUID userId) {
        if (userId == null) return null;
        return (root, query, cb) -> {
            Predicate hasPlaces = cb.greaterThan(root.get("availablePlaces"), 0);

            Subquery<Integer> sub = query.subquery(Integer.class);
            Root<EventEntity> e2 = sub.from(EventEntity.class);
            Join<EventEntity, UserEntity> u = e2.join("users");
            sub.select(cb.literal(1));
            sub.where(cb.equal(e2, root), cb.equal(u.get("id"), userId));
            Predicate notRegistered = cb.not(cb.exists(sub));

            return cb.and(hasPlaces, notRegistered);
        };
    }

    public static Specification<EventEntity> withStatus(String status, UUID userId) {
        if (status == null || userId == null) return null;
        return switch (status.toLowerCase()) {
            case "registered" -> registeredBy(userId);
            case "available" -> availableFor(userId);
            default -> null;
        };
    }
}