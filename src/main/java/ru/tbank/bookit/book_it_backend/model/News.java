package ru.tbank.bookit.book_it_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "NEWS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private NewsTag tags;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}