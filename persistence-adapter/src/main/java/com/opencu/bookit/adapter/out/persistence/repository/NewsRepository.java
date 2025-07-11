package com.opencu.bookit.adapter.out.persistence.repository;

import com.opencu.bookit.adapter.out.persistence.entity.NewsEntity;
import com.opencu.bookit.domain.model.event.ThemeTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface NewsRepository extends JpaRepository<NewsEntity, UUID> {
    @Query("""
    SELECT DISTINCT e 
    FROM NewsEntity e 
    JOIN e.tags t 
    WHERE t IN :tags 
    ORDER BY e.createdAt DESC
""")
    List<NewsEntity> findByTagsIn(@Param("tags") Set<ThemeTags> tags);
}
