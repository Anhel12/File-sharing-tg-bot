package com.example.dispatcher.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


public interface AnswerConsumer {
    void consume(SendMessage sendMessage);
}
