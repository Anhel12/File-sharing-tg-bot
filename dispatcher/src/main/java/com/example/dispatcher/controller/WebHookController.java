package com.example.dispatcher.controller;

import com.example.dispatcher.utils.TelegramUpdateDeserializer;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WebHookController {

    private final UpdateProcessor updateProcessor;
    private final TelegramUpdateDeserializer telegramUpdateDeserializer;

    @PostMapping("/callback/update")
    public ResponseEntity<?> onUpdateReceived(@RequestBody String body) {
        var update = telegramUpdateDeserializer.deserialize(body);
        updateProcessor.processUpdate(update);
        return ResponseEntity.ok().build();
    }
}
