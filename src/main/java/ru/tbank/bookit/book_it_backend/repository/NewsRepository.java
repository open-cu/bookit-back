package ru.tbank.bookit.book_it_backend.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.tbank.bookit.book_it_backend.model.News;
import ru.tbank.bookit.book_it_backend.model.NewsTag;


import java.util.List;
import java.util.Set;

public interface NewsRepository extends CrudRepository<News, String> {
    @Query("SELECT n FROM News n WHERE n.tags IN :tags ORDER BY n.createdAt DESC")
    List<News> findByTagsIn(@Param("tags") Set<NewsTag> tags);
}
