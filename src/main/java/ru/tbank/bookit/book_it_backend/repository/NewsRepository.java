package ru.tbank.bookit.book_it_backend.repository;

import org.springframework.data.repository.CrudRepository;
import ru.tbank.bookit.book_it_backend.model.News;

public interface NewsRepository extends CrudRepository<News, Integer> {
}
