package com.example.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Getter
@Configuration
public class RabbitConfiguration {

    @Value("${spring.rabbitmq.queues.registration-mail}")
    private String registrationMailQueue;

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public Queue registrationMailQueue() {
        return new Queue(registrationMailQueue);
    }
}
