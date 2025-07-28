package com.opencu.bookit.adapter.out.nofication.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.delayed}")
    private String delayedExchangeName;

    @Value("${rabbitmq.queue.notifications}")
    private String notificationsQueueName;

    @Value("${rabbitmq.queue.routing-key}")
    private String routingKey;

    @Bean
    public Queue notificationsQueue() {
        return new Queue(notificationsQueueName, true);
    }

    @Bean
    public CustomExchange delayedExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(delayedExchangeName, "x-delayed-message", true, false, args);
    }

    @Bean
    public Binding binding(Queue notificationsQueue, CustomExchange delayedExchange) {
        return BindingBuilder.bind(notificationsQueue)
                .to(delayedExchange)
                .with(routingKey)
                .noargs();
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}