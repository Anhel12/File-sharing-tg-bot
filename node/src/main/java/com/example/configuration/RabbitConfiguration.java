package com.example.configuration;

import lombok.Getter;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class RabbitConfiguration {

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new JacksonJsonMessageConverter();
    }
}
