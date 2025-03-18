package com.ebs.boardparadice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    // WebConfig에 따라 외부 경로로 저장 (예: /home/ubuntu/uploads/news/)
    private static final String UPLOAD_DIR = "/home/ubuntu/uploads/news/";
    // BASE_URL은 실제 외부 도메인이나 IP로 변경하세요.
    private static final String BASE_URL = "http://43.202.30.85";

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            // 파일명 생성 (UUID + 원본 파일명, 공백은 언더바로 치환)
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename().replaceAll("[\\s]", "_");
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            // 클라이언트가 접근 가능한 URL 생성
            String imageUrl = BASE_URL + "/uploads/news/" + fileName;
            return ResponseEntity.ok().body(Map.of("imageUrl", imageUrl));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "파일 업로드 실패: " + e.getMessage()));
        }
    }
}
