package ru.tbank.bookit.book_it_backend.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic bookTopic() {
        return TopicBuilder.name("my-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
