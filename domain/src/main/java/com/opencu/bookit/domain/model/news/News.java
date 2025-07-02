package com.opencu.bookit.domain.model.news;

import com.opencu.bookit.domain.model.event.ThemeTags;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class News {
    private UUID id;
    private String title;
    private String description;
    private Set<ThemeTags> tags = new HashSet<>();
    private LocalDateTime createdAt;
}