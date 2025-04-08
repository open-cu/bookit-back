package ru.tbank.bookit.book_it_backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tbank.bookit.book_it_backend.model.News;
import ru.tbank.bookit.book_it_backend.model.NewsTag;
import ru.tbank.bookit.book_it_backend.repository.NewsRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Configuration
public class NewsDataInitializer {

    @Bean
    CommandLineRunner initNews(NewsRepository newsRepository) {
        return args -> {
            if (newsRepository.count() == 0) {
                News spaceX = new News();
                spaceX.setTitle("Space X");
                spaceX.setDescription("Elon Musk visited a coworking space and launched a taxi service for delivery from anywhere in St. Petersburg.");
                spaceX.setTags(NewsTag.TECHNOLOGY);
                spaceX.setTags(NewsTag.IT);
                spaceX.setCreatedAt(LocalDateTime.parse("2025-04-04T10:00:00"));

                News maintenance = new News();
                maintenance.setTitle("System Maintenance Scheduled");
                maintenance.setDescription("Our platform will undergo scheduled maintenance on April 10th from 2 AM to 4 AM. Some services may be temporarily unavailable.");
                maintenance.setTags(NewsTag.IT);
                maintenance.setCreatedAt(LocalDateTime.parse("2025-04-03T08:15:00"));

                newsRepository.saveAll(List.of(spaceX, maintenance));
                System.out.println("Initial news created successfully");
            }
        };
    }
}
