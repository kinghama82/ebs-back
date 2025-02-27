package com.ebs.boardparadice.controller.boards;

import com.ebs.boardparadice.DTO.PageRequestDTO;
import com.ebs.boardparadice.DTO.PageResponseDTO;
import com.ebs.boardparadice.DTO.boards.NewsDTO;
import com.ebs.boardparadice.DTO.boards.RulebookDTO;
import com.ebs.boardparadice.service.boards.RulebookService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
            String uploadDir = "uploads/";
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
        public ImageUploadResponse(String url) { this.url = url; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
    }



    @GetMapping("/list")
    public PageResponseDTO<RulebookDTO> list(PageRequestDTO pageRequestDTO){

        return rulebookService.getList(pageRequestDTO);
    }

    @GetMapping("/{id}")
    public RulebookDTO get(@PathVariable(name="id") Integer id){
        return rulebookService.getRulebook(id);
    }

    @PostMapping("/create")
    public Map<String, Integer> create(@RequestPart("rulebook") RulebookDTO rulebookDTO, @RequestPart(value = "image", required = false) MultipartFile image) {
        if (image != null) {
            String imageUrl = rulebookService.uploadImage(image);
            rulebookDTO.setImageUrl(imageUrl);
        }
        Integer id = rulebookService.createRulebook(rulebookDTO);
        return Map.of("id", id);
    }

    @PutMapping("/{id}")
    public Map<String, String> modify(@PathVariable(name="id") Integer id, RulebookDTO rulebookDTO){

        rulebookDTO.setId(id);

        rulebookService.modifyRulebook(rulebookDTO);
        return Map.of("result", "성공");

    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable(name="id") Integer id){

        rulebookService.deleteRulebook(id);
        return Map.of("결과", "성공");
    }


}
