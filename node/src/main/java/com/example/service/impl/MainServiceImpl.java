package com.example.service.impl;

import com.example.dao.AppUserDao;
import com.example.dao.RawDataDao;
import com.example.entity.AppDocument;
import com.example.entity.AppPhoto;
import com.example.entity.AppUser;
import com.example.entity.RawData;
import com.example.entity.enums.UserState;
import com.example.exceptions.BigFileException;
import com.example.exceptions.UploadFileException;
import com.example.service.FileService;
import com.example.service.MainService;
import com.example.service.ProducerService;
import com.example.service.enums.LinkType;
import com.example.service.enums.ServiceCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static com.example.entity.enums.UserState.BASIC_STATE;
import static com.example.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static com.example.service.enums.ServiceCommands.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class MainServiceImpl implements MainService {
    private final RawDataDao rawDataDao;
    private final ProducerService producerService;
    private final AppUserDao appUserDao;
    private final FileService fileService;

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";

        var serviceCommand = ServiceCommands.fromValue(text);
        if (CANCEL.equals(serviceCommand)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, serviceCommand);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            //TODO Добавить обработку емайла
        } else {
            log.error("Unknow user state: " + userState);
            output = "Неизвестная ошибка! Введите /cancel и попробуйте снова!";
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if(isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppDocument doc = fileService.processDoc(update.getMessage());
            String link = fileService.generateLink(doc.getId(), LinkType.GET_DOC);
            var answer = "Документ успешно загружен! " +
                    "Ссылка для скачивания: " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException e) {
            log.error(String.valueOf(e));
            String error = "К сожалению, загрузка файла не удалась. Повторите попытку позже.";
            sendAnswer(error, chatId);
        } catch (BigFileException e) {
            String error = "К сожалению, ты пидр, отправивший большой файл. Повторите попытку позже.";
            sendAnswer(error, chatId);
        }
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if(isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppPhoto photo = fileService.processPhoto(update.getMessage());
            String link = fileService.generateLink(photo.getId(), LinkType.GET_PHOTO);
            var answer = "Фото успешно загружено! " +
                    "Ссылка для скачивания: " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException e) {
            log.error(String.valueOf(e));
            String error = "К сожалению, загрузка фото не удалась. Повторите попытку позже.";
            sendAnswer(error, chatId);
        }
    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getState();
        if (!appUser.getIsActive()) {
            var error = "Зарегистрируйтесь или активируйте свою учетную запись для загрузки контента.";
            sendAnswer(error, chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            var error = "Отмените текущую команду с помощью /cancel для отправки файлов.";
            sendAnswer(error, chatId);
            return true;
        }
        return false;
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, ServiceCommands cmd) {
        if (REGISTRATION.equals(cmd)){
            //TODO добавить регистрацию
            return "Временно недоступно!";
        } else if (HELP.equals(cmd)){
            return help();
        } else if (START.equals(cmd)) {
            return "Привет! Чтобы посмотреть список доступных команд введите /help";
        } else {
            return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
        }
    }

    private String help() {
        return "Список доступных команд:\n" +
                "/cancel - Отмена выполнения текущей команды;\n" +
                "/registration - регистрация пользователя;\n";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserDao.save(appUser);
        return "Команда отменена!";
    }

    private AppUser findOrSaveAppUser(Update update){
        User telegramUser = update.getMessage().getFrom();

        AppUser persistentAppUser = appUserDao.findAppUserByTelegramId(telegramUser.getId());
        if(persistentAppUser == null) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActive(true)
                    .state(BASIC_STATE)
                    .build();
            return appUserDao.save(transientAppUser);
        }
        return persistentAppUser;
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDao.save(rawData);
    }
}
