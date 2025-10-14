package com.opencu.bookit.adapter.out.persistence.specifications;

import com.opencu.bookit.adapter.out.persistence.entity.NewsEntity;
import com.opencu.bookit.domain.model.contentcategory.ThemeTags;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;

public final class NewsSpecifications {

    private NewsSpecifications() {}

    public static Specification<NewsEntity> hasAnyTags(Set<ThemeTags> tags) {
        if (tags == null || tags.isEmpty()) return null;
        return (root, query, cb) -> {
            Subquery<Integer> sub = query.subquery(Integer.class);
            Root<NewsEntity> n2 = sub.from(NewsEntity.class);
            Join<NewsEntity, ThemeTags> t = n2.join("tags");
            sub.select(cb.literal(1));
            sub.where(
                cb.equal(n2, root),
                t.in(tags)
            );
            return cb.exists(sub);
        };
    }

    public static Specification<NewsEntity> search(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String pattern = toLikePattern(raw);

        return (root, query, cb) -> {
            Expression<String> title = cb.lower(root.get("title"));
            Expression<String> shortDesc = cb.lower(root.get("short_description"));
            Expression<String> fullDesc = cb.lower(root.get("full_description"));

            return cb.or(
                cb.like(title, pattern, '\\'),
                cb.like(shortDesc, pattern, '\\'),
                cb.like(fullDesc, pattern, '\\')
            );
        };
    }

    private static String toLikePattern(String input) {
        String s = input.trim().toLowerCase()
           .replace("\\", "\\\\")
           .replace("%", "\\%")
           .replace("_", "\\_");
        return "%" + s + "%";
    }
}