package com.example.service.enums;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
public enum ServiceCommands {
    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start"),
    CHANGE("/changeemail");
    private final String value;

    @Override
    public String toString() {
        return value;
    }

    public static ServiceCommands fromValue(String v) {
        for (ServiceCommands command : ServiceCommands.values()) {
            if(command.value.equals(v)) {
                return command;
            }
        }
        return null;
    }
}
