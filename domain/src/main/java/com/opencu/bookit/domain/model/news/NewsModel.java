package com.opencu.bookit.domain.model.news;

import com.opencu.bookit.domain.model.contentcategory.ThemeTags;
import lombok.*;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsModel {
    private UUID id;
    private String title;
    private Optional<String> shortDescription;
    private String fullDescription;
    private Set<ThemeTags> tags = new HashSet<>();
    private List<String> keys;
    private LocalDateTime createdAt;
}