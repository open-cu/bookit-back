package ru.tbank.bookit.book_it_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.bookit.book_it_backend.model.News;
import ru.tbank.bookit.book_it_backend.model.ThemeTags;
import ru.tbank.bookit.book_it_backend.repository.NewsRepository;

import java.util.List;
import java.util.Set;

@Service
public class NewsService {
    private final NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public List<News> findAll() {
        return (List<News>) newsRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<News> findByTags(Set<ThemeTags> tags){
        return newsRepository.findByTagsIn(tags);
    }

    public Page<News> findWithFilters(Set<ThemeTags> tags, String search, Pageable pageable, Specification<News> spec) {
        return newsRepository.findAll(spec, pageable);
    }

    public Specification<News> buildSpecification(Set<ThemeTags> tags, String search) {
        Specification<News> spec = Specification.where(null);
        if (tags != null && !tags.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.join("tags").in(tags));
        }
        if (search != null && !search.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("title")), "%" + search.toLowerCase() + "%"),
                            cb.like(root.get("description"), "%" + search + "%")
                    )
            );
        }
        return spec;
    }
}
