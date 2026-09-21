package com.example.service.impl;

import com.example.service.ConsumerService;
import com.example.service.MainService;
import com.example.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.example.commonrabbitmq.RabbitQueue.DOC_MESSAGE_UPDATE;
import static com.example.commonrabbitmq.RabbitQueue.TEXT_MESSAGE_UPDATE;
import static com.example.commonrabbitmq.RabbitQueue.PHOTO_MESSAGE_UPDATE;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {
    private final MainService mainService;

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessageUpdates(Update update) {
        log.debug("Node: Text message is received");
        mainService.processTextMessage(update);
    }

    @Override
    @RabbitListener(queues = DOC_MESSAGE_UPDATE)
    public void consumeDocMessageUpdates(Update update) {
        log.debug("Node: Doc message is received");
        mainService.processDocMessage(update);
    }

    @Override
    @RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
    public void consumePhotoMessageUpdates(Update update) {
        log.debug("Node: Photo message is received");
        mainService.processPhotoMessage(update);
    }
}
