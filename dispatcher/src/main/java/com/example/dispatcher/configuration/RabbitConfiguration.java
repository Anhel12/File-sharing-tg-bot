package com.example.dispatcher.configuration;

import lombok.Getter;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class RabbitConfiguration {

    @Value("${spring.rabbitmq.queues.text-message-update}")
    private String textMessageUpdate;
    @Value("${spring.rabbitmq.queues.doc-message-update}")
    private String docMessageUpdate;
    @Value("${spring.rabbitmq.queues.photo-message-update}")
    private String photoMessageUpdate;
    @Value("${spring.rabbitmq.queues.answer-message}")
    private String answerMessage;

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public Queue textMessageQueue() {
        return new Queue(textMessageUpdate);
    }

    @Bean
    public Queue docMessageQueue() {
        return new Queue(docMessageUpdate);
    }

    @Bean
    public Queue photoMessageQueue() {
        return new Queue(photoMessageUpdate);
    }

    @Bean
    public Queue answerMessageQueue() {
        return new Queue(answerMessage);
    }
}
