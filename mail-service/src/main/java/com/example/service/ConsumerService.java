package com.example.service;

import com.example.dto.MailParams;

public interface ConsumerService {

    void consumeRegistrationMail(MailParams mailParams);
}
