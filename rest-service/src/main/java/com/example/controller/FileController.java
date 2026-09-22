package com.example.controller;

import com.example.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/file")
public class FileController {
    private final FileService fileService;

    @GetMapping("/get-doc")
    public ResponseEntity<?> getDoc(@RequestParam("id") String id) {
        var doc = fileService.getDocument(id);
        if(doc == null) {
            return ResponseEntity.badRequest().build();
        }
         var binaryContent = doc.getBinaryContent();

        var fileSystemResource = fileService.getFileSystemresource(binaryContent);
        if(fileSystemResource == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getMimeType()))
                .header("Content-disposition", "attachment; filename=" + doc.getDocName())
                .body(fileSystemResource);
    }

    @GetMapping("/get-photo")
    public ResponseEntity<?> getPhoto(@RequestParam("id") String id) {
        var photo = fileService.getPhoto(id);
        if(photo == null) {
            return ResponseEntity.badRequest().build();
        }
        var binaryContent = photo.getBinaryContent();

        var fileSystemResource = fileService.getFileSystemresource(binaryContent);
        if(fileSystemResource == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header("Content-disposition", "attachment;")
                .body(fileSystemResource);
    }
}
