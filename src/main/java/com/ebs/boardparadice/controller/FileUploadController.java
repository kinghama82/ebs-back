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

    // 파일이 저장될 경로 (NewsController에서 사용한 경로와 동일)
    private static final String UPLOAD_DIR = "D:/ebs-back/src/main/resources/static/uploads/news/";

    // 업로드 후 클라이언트가 접근할 수 있는 절대 URL을 생성합니다.
    private static final String BASE_URL = "http://localhost:8080"; // 필요한 경우 도메인 변경

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

            // 절대 URL 생성
            String imageUrl = BASE_URL + "/uploads/news/" + fileName;
            return ResponseEntity.ok().body(Map.of("imageUrl", imageUrl));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "파일 업로드 실패: " + e.getMessage()));
        }
    }
}