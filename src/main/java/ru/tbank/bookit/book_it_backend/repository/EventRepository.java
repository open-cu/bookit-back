package ru.tbank.bookit.book_it_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.tbank.bookit.book_it_backend.model.Event;
import ru.tbank.bookit.book_it_backend.model.NewsTag;

import java.util.List;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, String> {
    @Query("SELECT n FROM Event n WHERE n.tags IN :tags ORDER BY n.date DESC")
    List<Event> findByTagsIn(@Param("tags") Set<NewsTag> tags);
}
