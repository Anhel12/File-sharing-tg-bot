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
import com.example.service.enums.LinkType;

import com.example.utils.DocCryptoTool;
import com.example.utils.PhotoCryptoTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

@Log4j2
@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

    @Value("${telegram.bot.token}")
    private String token;
    @Value("${telegram.service.file_info.uri}")
    private String fileInfoUri;
    @Value("${telegram.service.file_storage.uri}")
    private String fileStorageUri;
    @Value("${link.address}")
    private String linkAddress;

    private final AppDocumentDao appDocumentDao;
    private final AppPhotoDao appPhotoDao;
    private final BinaryContentDao binaryContentDao;
    private final DocCryptoTool docCryptoTool;
    private final PhotoCryptoTool photoCryptoTool;

    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20 МБ

    @Override
    public AppDocument processDoc(Message telegramMessage) {
        var telegramDoc = telegramMessage.getDocument();
        var fileId = telegramDoc.getFileId();
        var fileSize = telegramDoc.getFileSize();

        if(fileSize > MAX_FILE_SIZE) {
            throw new BigFileException("file is too big");
        } else {
            var response = getFilePath(fileId);
            if(response.getStatusCode() == HttpStatus.OK) {
                var binaryContent = getPersistentBinaryContent(response);
                var transientAppDocument = buildTransientAppDoc(telegramDoc, binaryContent);
                return appDocumentDao.save(transientAppDocument);
            } else {
                throw new UploadFileException("Bad response from telegram service: " + response);
            }
        }
    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage) {
        var lastPhoto = telegramMessage.getPhoto().size() - 1;
        var telegramPhoto = telegramMessage.getPhoto().get(lastPhoto);
        var fileId = telegramPhoto.getFileId();
        var response = getFilePath(fileId);
        if(response.getStatusCode() == HttpStatus.OK) {
            var binaryContent = getPersistentBinaryContent(response);
            var transientAppDocument = buildTransientAppPhoto(telegramPhoto, binaryContent);
            return appPhotoDao.save(transientAppDocument);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) {
        var filePath = getFilePath(response);
        var fileInBytes = downloadFile(filePath);
        var transientBinaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(fileInBytes)
                .build();
        return binaryContentDao.save(transientBinaryContent);
    }
    private String getFilePath(ResponseEntity<String> response){
        var jsonObject = new JSONObject(response.getBody());
        var filePath = String.valueOf(jsonObject
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
        var fullUri = fileStorageUri.replace("{token}", token)
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
        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        var request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token,
                fileId
        );
    }

    @Override
    public String generateLink(Long fileId, LinkType linkType) {
        String hash;
        if(linkType.name().equals("GET_DOC")) {
            hash = docCryptoTool.hashOf(fileId);
        } else {
            hash = photoCryptoTool.hashOf(fileId);
        }
        return linkAddress + "/api/" + linkType + "?id=" + hash;
    }
}
