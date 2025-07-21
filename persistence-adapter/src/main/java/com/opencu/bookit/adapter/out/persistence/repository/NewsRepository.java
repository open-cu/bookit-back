package com.opencu.bookit.adapter.out.persistence.repository;

import com.opencu.bookit.adapter.out.persistence.entity.NewsEntity;
import com.opencu.bookit.domain.model.contentcategory.ThemeTags;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface NewsRepository extends CrudRepository<NewsEntity, UUID>, JpaSpecificationExecutor<NewsEntity> {
    @Query("""
    SELECT DISTINCT e 
    FROM NewsEntity e 
    JOIN e.tags t 
    WHERE t IN :tags 
    ORDER BY e.createdAt DESC
""")
    List<NewsEntity> findByTagsIn(@Param("tags") Set<ThemeTags> tags);

    @Override
    Optional<NewsEntity> findById(UUID newsId);
}
