package com.ebs.boardparadice.controller.boards;

import com.ebs.boardparadice.DTO.PageRequestDTO;
import com.ebs.boardparadice.DTO.PageResponseDTO;
import com.ebs.boardparadice.DTO.boards.RulebookDTO;
import com.ebs.boardparadice.repository.boards.RulebookRepository;
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
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rulebook")
public class RuleBookController {

    private final RulebookService rulebookService;


    // 이미지 업로드
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = rulebookService.uploadImage(file);
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


    /*@PostMapping("/create")
    public Map<String, Integer> create(@RequestPart("rulebook") MultipartFile rulebookJsonFile) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            RulebookDTO rulebookDTO = objectMapper.readValue(rulebookJsonFile.getInputStream(), RulebookDTO.class);

            System.out.println("받은 데이터: " + rulebookDTO);

            Integer id = rulebookService.createRulebook(rulebookDTO);
            return Map.of("id", id);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("id", -1);
        }
    }*/

    @PostMapping("/create")
    public Map<String, Integer> create(@RequestBody RulebookDTO rulebookDTO) {
        try {
            // rulebookDTO가 null인지 체크
            if (rulebookDTO == null) {
                return Map.of("id", -1);  // 데이터가 없을 경우
            }

            System.out.println("받은 rulebookDTO: {}" +  rulebookDTO);  // 데이터가 정상적으로 왔는지 로그 찍기
            Integer id = rulebookService.createRulebook(rulebookDTO);
            return Map.of("id", id);
        } catch (Exception e) {
            System.out.println("게시글 생성 중 에러 발생!" + e);
            return Map.of("id", -1);  // 예외 발생시 id -1 반환
        }
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
