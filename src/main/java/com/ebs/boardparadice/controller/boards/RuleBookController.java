package com.ebs.boardparadice.controller.boards;

import com.ebs.boardparadice.DTO.PageRequestDTO;
import com.ebs.boardparadice.DTO.PageResponseDTO;
import com.ebs.boardparadice.DTO.boards.RulebookDTO;
import com.ebs.boardparadice.service.boards.RulebookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rulebook")
public class RuleBookController {

    private final RulebookService rulebookService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("파일 업로드 요청");
            String uploadDir = "/src/main/resources/static/upload/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.write(filePath, file.getBytes());

            String fileUrl = "http://localhost:8080/" + uploadDir + fileName;
            return ResponseEntity.ok(new ImageUploadResponse(fileUrl));
        } catch (IOException e) {
            return ResponseEntity.status(500).body("파일 업로드 실패");
        }
    }

    public static class ImageUploadResponse {
        private String url;

        public ImageUploadResponse(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    @GetMapping("/list")
    public PageResponseDTO<RulebookDTO> list(PageRequestDTO pageRequestDTO) {

        return rulebookService.getList(pageRequestDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RulebookDTO> get(@PathVariable(name = "id") Integer id) {
        // 게시글을 조회하면서 조회수를 증가시킴
        RulebookDTO rulebookDTO = rulebookService.getRulebook(id);
    
        // 조회수 증가 후, 게시글을 저장하는 로직을 서비스에서 처리
        rulebookService.incrementViewCount(id);
    
        return ResponseEntity.ok(rulebookDTO);  // 조회수 증가 후, 게시글 반환
    }

    @PostMapping("/create")
    public Map<String, Integer> create(@RequestPart("rulebook") RulebookDTO rulebookDTO,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        Integer id = rulebookService.createRulebook(rulebookDTO);
        System.out.println("-------------------------------------123");
        return Map.of("id", id);
    }

    @PutMapping("/modify/{id}")
    public Map<String, String> modify(
            @PathVariable(name = "id") Integer id,
            @RequestPart("rulebook") String rulebookJson) throws IOException {

        // JSON을 RulebookDTO로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        RulebookDTO rulebookDTO = objectMapper.readValue(rulebookJson, RulebookDTO.class);

        // ID 설정
        rulebookDTO.setId(id);

        // 서비스 호출
        rulebookService.modifyRulebook(rulebookDTO);

        return Map.of("result", "성공");
    }

    @DeleteMapping("/delete/{id}")
    public Map<String, String> delete(@PathVariable(name = "id") Integer id) {
        System.out.println("삭제 요청 받음: " + id);
        rulebookService.deleteRulebook(id);
        return Map.of("결과", "성공");
    }

     // 조회수 증가
     @PostMapping("/{id}/view")
     public ResponseEntity<String> incrementViewCount(@PathVariable Integer id) {
         rulebookService.incrementViewCount(id);
         return ResponseEntity.ok("View count incremented");
     }
}
