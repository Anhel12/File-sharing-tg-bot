package com.example.dispatcher.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j2
@Component
@RequiredArgsConstructor
public class TelegramUpdateDeserializer {

    private final ObjectMapper objectMapper;

    public Update deserialize(String body) {
        try {
            return objectMapper.readValue(body, Update.class);
        } catch (JsonMappingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
