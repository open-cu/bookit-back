package com.opencu.bookit.adapter.out.persistence.entity;

import com.opencu.bookit.domain.model.contentcategory.ThemeTags;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "NEWS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String full_description;

    @Column
    private String short_description;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "news_tags",
            joinColumns = @JoinColumn(name = "news_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "tag")
    private Set<ThemeTags> tags = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "news_keys", joinColumns = @JoinColumn(name = "news_id"))
    @Column(nullable = false)
    private List<String> keys;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}