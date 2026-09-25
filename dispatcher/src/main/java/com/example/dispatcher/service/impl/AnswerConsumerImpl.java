package com.example.dispatcher.service.impl;

import com.example.dispatcher.controller.UpdateProcessor;
import com.example.dispatcher.service.AnswerConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
@RequiredArgsConstructor
public class AnswerConsumerImpl implements AnswerConsumer {

    private final UpdateProcessor updateProcessor;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.answer-message}")
    public void consume(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage);
    }
}
