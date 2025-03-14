package com.ebs.boardparadice.controller.boards;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ebs.boardparadice.DTO.PageRequestDTO;
import com.ebs.boardparadice.DTO.PageResponseDTO;
import com.ebs.boardparadice.DTO.boards.NewsDTO;
import com.ebs.boardparadice.service.boards.NewsService;

import jakarta.servlet.MultipartConfigElement;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor

public class NewsController {

    private static final String UPLOAD_DIR = "D:/ebs-back/src/main/resources/static/uploads/news/";

    private final NewsService newsService;

    // ✅ 뉴스 조회
    @GetMapping("/{id}")
    public ResponseEntity<NewsDTO> getNews(@PathVariable Integer id) {
        return ResponseEntity.ok(newsService.getNews(id));
    }

    // ✅ 뉴스 목록 조회 (페이징)
    // ✅ GET 요청에서 multipart를 사용하지 않도록 수정
    @GetMapping("/list")
    public ResponseEntity<PageResponseDTO<NewsDTO>> getNewsList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequestDTO pageRequestDTO = new PageRequestDTO(page, size);
        return ResponseEntity.ok(newsService.getlist(pageRequestDTO));
    }


    // ✅ 뉴스 작성
    @PostMapping("/")
    public ResponseEntity<?> createNews(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("writerId") Integer writerId,
//            @RequestParam(value = "youtubeUrl", required = false) String youtubeUrl,
            @RequestParam(value = "youtubeUrl", required = false) String youtubeUrl,
            @RequestParam(value = "images[]", required = false) MultipartFile[] images) { // ✅ `images[]`로 수정

        try {
            System.out.println("=== FormData Debugging ===");
            System.out.println("Title: " + title);
            System.out.println("Content: " + content);
            System.out.println("Writer ID: " + writerId);
            System.out.println("YouTube URL: " + youtubeUrl);
            System.out.println("Images: " + (images != null ? images.length : "No Images"));

            List<String> imageUrls = new ArrayList<>();
            if (images != null && images.length > 0) {
                for (MultipartFile imgFile : images) {
                    String filePath = saveImageFile(imgFile);
                    imageUrls.add(filePath);
                }
            }

            NewsDTO newsDTO = NewsDTO.builder()
                    .title(title)
                    .content(content)
                    .writerId(writerId)
                    .youtubeUrl(youtubeUrl)
                    .imageUrls(imageUrls)
                    .build();

            Integer newsId = newsService.createNews(newsDTO);
            return ResponseEntity.ok().body(Map.of("id", newsId));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("게시글 생성 실패: " + e.getMessage());
        }
    }

    // ✅ 뉴스 수정
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> modifyNews(@PathVariable Integer id, @RequestBody NewsDTO newsDTO) {
        newsDTO.setId(id);
        newsService.modifyNews(newsDTO);
        return ResponseEntity.ok(Map.of("result", "성공"));
    }

    // ✅ 뉴스 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteNews(@PathVariable Integer id) {
        newsService.deleteNews(id);
        return ResponseEntity.ok(Map.of("결과", "성공"));
    }

    /**
     * ✅ 이미지 저장 메서드
     */
    private String saveImageFile(MultipartFile imgFile) throws Exception {
        Path uploadPath = Paths.get(UPLOAD_DIR);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID().toString() + "_" + imgFile.getOriginalFilename().replaceAll("[\\s]", "_");
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(imgFile.getInputStream(), filePath);

        return "/uploads/news/" + fileName; // ✅ 저장된 이미지 경로 반환
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(10)); // 최대 파일 크기 10MB
        factory.setMaxRequestSize(DataSize.ofMegabytes(50)); // 요청당 최대 50MB
        return factory.createMultipartConfig();
    }

}
