package com.example.service.impl;

import com.example.dao.AppDocumentDao;
import com.example.dao.AppPhotoDao;
import com.example.dao.BinaryContentDao;
import com.example.entity.AppDocument;
import com.example.entity.AppPhoto;
import com.example.entity.BinaryContent;
import com.example.exceptions.BigFileException;
import com.example.exceptions.UploadFileException;
import com.example.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    @Value("${telegram.bot.token}")
    private String token;
    @Value("${telegram.service.file_info.uri}")
    private String fileInfoUri;
    @Value("${telegram.service.file_storage.uri}")
    private String fileStorageUri;

    private final AppDocumentDao appDocumentDao;
    private final AppPhotoDao appPhotoDao;
    private final BinaryContentDao binaryContentDao;

    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20 МБ

    @Override
    public AppDocument processDoc(Message telegramMessage) {
        Document telegramDoc = telegramMessage.getDocument();
        String fileId = telegramDoc.getFileId();
        Long fileSize = telegramDoc.getFileSize();

        if(fileSize > MAX_FILE_SIZE) {
            throw new BigFileException("file is too big");
        } else {
            ResponseEntity<String> response = getFilePath(fileId);
            if(response.getStatusCode() == HttpStatus.OK) {
                BinaryContent binaryContent = getPersistentBinaryContent(response);
                AppDocument transientAppDocument = buildTransientAppDoc(telegramDoc, binaryContent);
                return appDocumentDao.save(transientAppDocument);
            } else {
                throw new UploadFileException("Bad response from telegram service: " + response);
            }
        }
    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage) {
        //TODO щас только первое фото обрабатывается(если будет отправлено сразу несколько фото)
        PhotoSize telegramPhoto = telegramMessage.getPhoto().getFirst();
        String fileId = telegramPhoto.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if(response.getStatusCode() == HttpStatus.OK) {
            BinaryContent binaryContent = getPersistentBinaryContent(response);
            AppPhoto transientAppDocument = buildTransientAppPhoto(telegramPhoto, binaryContent);
            return appPhotoDao.save(transientAppDocument);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) {
        String filePath = getFilePath(response);
        byte[] fileInBytes = downloadFile(filePath);
        BinaryContent transientBinaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(fileInBytes)
                .build();
        return binaryContentDao.save(transientBinaryContent);
    }
    private String getFilePath(ResponseEntity<String> response){
        JSONObject jsonObject = new JSONObject(response.getBody());
        String filePath = String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
        return filePath;
    }
    private AppDocument buildTransientAppDoc(Document telegramDoc, BinaryContent persistentBinaryContent) {
        return AppDocument.builder()
                .telegramFileId(telegramDoc.getFileId())
                .docName(telegramDoc.getFileName())
                .binaryContent(persistentBinaryContent)
                .mimeType(telegramDoc.getMimeType())
                .fileSize(telegramDoc.getFileSize())
                .build();
    }

    private AppPhoto buildTransientAppPhoto(PhotoSize telegramPhoto, BinaryContent persistentBinaryContent) {
        return AppPhoto.builder()
                .telegramFileId(telegramPhoto.getFileId())
                .binaryContent(persistentBinaryContent)
                .fileSize(telegramPhoto.getFileSize())
                .build();
    }

    private byte[] downloadFile(String filePath) {
        String fullUri = fileStorageUri.replace("{token}", token)
                .replace("{filePath}", filePath);
        URI urlObj = null;
        try {
            urlObj = new URI(fullUri);
        } catch (URISyntaxException e) {
            throw new UploadFileException(e);
        }

        //TODO подумать над оптимизацией
        try (InputStream is = urlObj.toURL().openStream()){
            return is.readAllBytes();
        } catch (IOException e) {
            throw new UploadFileException(e);
        }
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token,
                fileId
        );
    }
}
