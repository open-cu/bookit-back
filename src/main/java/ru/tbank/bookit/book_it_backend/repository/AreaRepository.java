package ru.tbank.bookit.book_it_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.bookit.book_it_backend.model.Area;
import ru.tbank.bookit.book_it_backend.model.AreaType;

import java.util.List;
import java.util.UUID;

public interface AreaRepository extends JpaRepository<Area, UUID> {
    List<Area> findByType(AreaType type);
}
