package com.example.service.impl;

import com.example.dao.AppUserDao;
import com.example.dto.MailParams;
import com.example.entity.AppUser;
import com.example.entity.enums.UserState;
import com.example.service.AppUserService;
import com.example.utils.UserCryptoTool;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AppUserServiceImpl implements AppUserService {

    private final AppUserDao appUserDao;
    private final UserCryptoTool userCryptoTool;
    private final RabbitTemplate rabbitTemplate;
    @Value("${telegram.service.mail.uri}")
    private String mailServiceUri;
    @Value("${spring.rabbitmq.queues.registration-mail}")
    private String registrationMailQueue;

    @Override
    public String registerUser(AppUser appUser) {
        if (appUser.getIsActive()){
            return "Вы уже зарегистрированы! " +
                    "Для получения ссылки вам нужно отправить файл!";
        } else if (appUser.getEmail() != null) {
            return "Вам на почту уже было отправлено письмо. " +
                    "Перейдите по ссылке в письме для подтверждения! " +
                    "Если почта не действительна и вы хотите ее заменить, " +
                    "введите /changeemail";
        }

        appUser.setState(UserState.WAIT_FOR_EMAIL_STATE);
        appUserDao.save(appUser);
        return "Введите, пожалуйста, ваш email:";
    }

    @Override
    public String setEmail(AppUser appUser, String email) {
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException e) {
            return "Введите, пожалуйста, корректный email. Для отмены команды введите /cancel";
        }
        var optional = appUserDao.findByEmail(email);
        if(optional.isEmpty()){
            appUser.setEmail(email);
            appUser.setState(UserState.BASIC_STATE);
            appUser = appUserDao.save(appUser);

            var cryptoUserId = userCryptoTool.hashOf(appUser.getId());
            sendRegistrationMail(cryptoUserId, email);
            return "Вам на почту было отправлено письмо. " +
                    "Перейдите по ссылке в письме для подтверждения регистрации. " +
                    "После подтверждения вы сможете загружать файлы для получения ссылок!";
        } else {
            return "Этот email уже используется. Введите корректный email. " +
                    "Для отмены команды введите /cancel";
        }
    }

    private void sendRegistrationMail(String cryptoUserId, String email) {
        var mailParams = MailParams.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();
        rabbitTemplate.convertAndSend(registrationMailQueue, mailParams);
    }
}
