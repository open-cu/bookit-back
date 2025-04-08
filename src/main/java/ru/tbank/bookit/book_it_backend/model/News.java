package ru.tbank.bookit.book_it_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "NEWS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "news_tags",
            joinColumns = @JoinColumn(name = "news_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "tag")
    private Set<ThemeTags> tags = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}