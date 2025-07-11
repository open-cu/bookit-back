package com.opencu.bookit.initializers;

import com.opencu.bookit.adapter.out.persistence.entity.NewsEntity;
import com.opencu.bookit.adapter.out.persistence.repository.NewsRepository;
import com.opencu.bookit.domain.model.event.ThemeTags;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Configuration
public class NewsDataInitializer {

    @Bean
    CommandLineRunner initNews(NewsRepository newsRepository) {
        return args -> {
            if (newsRepository.count() == 0) {
                NewsEntity spaceX = new NewsEntity();
                spaceX.setTitle("Space X");
                spaceX.setDescription("Elon Musk visited a coworking space and launched a taxi service for delivery from anywhere in St. Petersburg.");
                spaceX.setTags(Set.of(ThemeTags.IT, ThemeTags.TECHNOLOGY));
                spaceX.setCreatedAt(LocalDateTime.parse("2025-04-04T10:00:00"));

                NewsEntity maintenance = new NewsEntity();
                maintenance.setTitle("System Maintenance Scheduled");
                maintenance.setDescription("Our platform will undergo scheduled maintenance on April 10th from 2 AM to 4 AM. Some services may be temporarily unavailable.");
                maintenance.setTags(Set.of(ThemeTags.SCIENCE, ThemeTags.TECHNOLOGY));
                maintenance.setCreatedAt(LocalDateTime.parse("2025-04-03T08:15:00"));

                newsRepository.saveAll(List.of(spaceX, maintenance));
                System.out.println("Initial news created successfully");
            }
        };
    }
}
