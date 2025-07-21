package com.opencu.bookit.domain.model.news;

import com.opencu.bookit.domain.model.contentcategory.ThemeTags;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsModel {
    private UUID id;
    private String title;
    private String description;
    private Set<ThemeTags> tags = new HashSet<>();
    private List<String> keys;
    private LocalDateTime createdAt;
}