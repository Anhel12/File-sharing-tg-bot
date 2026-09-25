package com.example.controller;

import com.example.service.FileService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/file")
public class FileController {
    private final FileService fileService;

    @GetMapping("/get-doc")
    public void getDoc(@RequestParam("id") String id, HttpServletResponse response) {
        var doc = fileService.getDocument(id);
        if(doc == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        response.setContentType(MediaType.parseMediaType(doc.getMimeType()).toString());
        response.setHeader("Content-Disposition", "attachment; filename=" + doc.getDocName());
        response.setStatus(HttpServletResponse.SC_OK);

        var binaryContent = doc.getBinaryContent();
        try (ServletOutputStream out = response.getOutputStream()){
            out.write(binaryContent.getFileAsArrayOfBytes());
        } catch (IOException e) {
            log.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-photo")
    public void getPhoto(@RequestParam("id") String id, HttpServletResponse response) {
        var photo = fileService.getPhoto(id);
        if(photo == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        response.setHeader("Content-Disposition", "attachment;");
        response.setStatus(HttpServletResponse.SC_OK);

        var binaryContent = photo.getBinaryContent();
        try (ServletOutputStream out = response.getOutputStream()) {
            out.write(binaryContent.getFileAsArrayOfBytes());
        } catch (IOException e) {
            log.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
