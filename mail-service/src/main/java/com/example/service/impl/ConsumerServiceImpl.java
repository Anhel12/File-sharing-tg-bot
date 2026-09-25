package com.example.service.impl;

import com.example.dto.MailParams;
import com.example.service.ConsumerService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {

    private final MailSenderServiceImpl mailSenderService;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.registration-mail}")
    public void consumeRegistrationMail(MailParams mailParams) {
        mailSenderService.send(mailParams);
    }
}
