package com.example.Yotoqxonagradle.controller;

import com.example.Yotoqxonagradle.entity.FileStorage;
import com.example.Yotoqxonagradle.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileUrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;

@RestController
@RequestMapping("/file")
public class FileController {
    @Value("${upload.folder}")
    private String uploadFolder;

    @Autowired
    FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity upload(@RequestParam("file")MultipartFile multipartFile){
        fileStorageService.save(multipartFile);
        return ResponseEntity.ok(multipartFile.getOriginalFilename()+" file yuklandi!");
    }

    @GetMapping("/preview/{hashId}")
    public ResponseEntity preview(@PathVariable String hashId) throws IOException {
        FileStorage fileStorage = fileStorageService.findByHashId(hashId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"inline; filename=\"" + URLEncoder.encode(fileStorage.getName()))
                .contentType(MediaType.parseMediaType(fileStorage.getContentType()))
                .contentLength(fileStorage.getFileSize())
                .body(new FileUrlResource(String.format("%s/%s",uploadFolder,fileStorage.getUploadPath())));

    }

    @GetMapping("/download/{hashId}")
    public ResponseEntity download(@PathVariable String hashId) throws IOException {
        FileStorage fileStorage = fileStorageService.findByHashId(hashId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + URLEncoder.encode(fileStorage.getName()))
                .contentType(MediaType.parseMediaType(fileStorage.getContentType()))
                .contentLength(fileStorage.getFileSize())
                .body(new FileUrlResource(String.format("%s/%s",uploadFolder,fileStorage.getUploadPath())));

    }

    @DeleteMapping("/delete/{hashId}")
    public ResponseEntity delete(@PathVariable String hashId){
        fileStorageService.delete(hashId);
        return ResponseEntity.ok("File o'chirildi!");
    }
}
