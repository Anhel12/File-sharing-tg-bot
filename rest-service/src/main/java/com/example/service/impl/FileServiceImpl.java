package com.example.service.impl;

import com.example.dao.AppDocumentDao;
import com.example.dao.AppPhotoDao;
import com.example.entity.AppDocument;
import com.example.entity.AppPhoto;
import com.example.service.FileService;
import com.example.utils.DocCryptoTool;
import com.example.utils.PhotoCryptoTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Log4j2
@Service
public class FileServiceImpl implements FileService {

    private final AppDocumentDao appDocumentDao;
    private final AppPhotoDao appPhotoDao;
    private final DocCryptoTool docCryptoTool;
    private final PhotoCryptoTool photoCryptoTool;

    @Override
    public AppDocument getDocument(String hash) {
        var id = docCryptoTool.idOf(hash);
        if(id == null) {
            return null;
        }

        return appDocumentDao.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String hash) {
        var id = photoCryptoTool.idOf(hash);
        if(id == null) {
            return null;
        }

        return appPhotoDao.findById(id).orElse(null);
    }
}
