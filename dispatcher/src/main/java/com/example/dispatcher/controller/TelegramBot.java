package com.example.dispatcher.controller;

import jakarta.annotation.PostConstruct;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
@Getter
@RequiredArgsConstructor
public class TelegramBot extends TelegramWebhookBot  {

    @Value("${telegram.bot.username}")
    private String botUsername;
    @Value("${telegram.bot.token}")
    private String botToken;
    @Value("${telegram.bot.uri}")
    private String botUri;

    private final UpdateProcessor updateProcessor;

    @PostConstruct
    public void init() {
        updateProcessor.registerBot(this);
        try {
            var setWebhook = SetWebhook.builder()
                    .url(botUri)
                    .build();
            this.setWebhook(setWebhook);
            log.info("Telegram bot успешно зарегистрирован: {}", botUsername);
        } catch (TelegramApiException e) {
            log.error("Ошибка регистрации бота: {}", e.getMessage(), e);
        }

    }

    @Override
    public String getBotPath() {
        return "/update";
    }

    public void sendAnswerMessage(SendMessage message){
        if(message != null){
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }
}
