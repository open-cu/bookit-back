package com.opencu.bookit.initializers;

import com.opencu.bookit.adapter.out.persistence.entity.NewsEntity;
import com.opencu.bookit.adapter.out.persistence.repository.NewsRepository;
import com.opencu.bookit.domain.model.contentcategory.ThemeTags;
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
                spaceX.setDescription("Илон Маск посетил коворкинг и запустил службу такси для доставки из любой точки Санкт-Петербурга.");
                spaceX.setTags(Set.of(ThemeTags.IT, ThemeTags.TECHNOLOGY));
                spaceX.setKeys(List.of("arch.png"));
                spaceX.setCreatedAt(LocalDateTime.parse("2025-04-04T10:00:00"));

                NewsEntity maintenance = new NewsEntity();
                maintenance.setTitle("Плановое обслуживание системы");
                maintenance.setDescription("10 апреля с 2:00 до 4:00 на нашей платформе пройдут плановые технические работы. Некоторые сервисы могут быть временно недоступны.");
                maintenance.setTags(Set.of(ThemeTags.SCIENCE, ThemeTags.TECHNOLOGY));
                maintenance.setKeys(List.of("arch.png"));
                maintenance.setCreatedAt(LocalDateTime.parse("2025-04-03T08:15:00"));

                newsRepository.saveAll(List.of(spaceX, maintenance));
                System.out.println("Initial news created successfully");
            }
        };
    }
}
