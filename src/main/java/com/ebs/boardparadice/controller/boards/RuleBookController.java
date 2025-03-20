package com.ebs.boardparadice.controller.boards;

import com.ebs.boardparadice.DTO.PageRequestDTO;
import com.ebs.boardparadice.DTO.PageResponseDTO;
import com.ebs.boardparadice.DTO.boards.FreeDTO;
import com.ebs.boardparadice.DTO.boards.RulebookDTO;
import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.model.boards.Rulebook;
import com.ebs.boardparadice.repository.GamerRepository;
import com.ebs.boardparadice.repository.boards.RulebookRepository;
import com.ebs.boardparadice.service.boards.RulebookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.digester.Rule;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final ModelMapper modelMapper;
    private final RulebookRepository rulebookRepository;

    @Autowired
    private final GamerRepository gamerRepository;


    // 이미지 업로드
    @PostMapping("/upload")
    public String uploadImage(MultipartFile file) throws IOException {
        // 업로드할 디렉토리 경로
        String uploadDir = "/home/ubuntu/uploads";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();  // 디렉토리가 없으면 생성
        }

        // 파일명과 경로 설정
        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        // 파일을 디스크에 저장
        Files.write(filePath, file.getBytes());

        // 로컬 URL 반환
        return "http://43.202.30.85:8080/uploads/" + fileName;  // 로컬 서버에서 접근할 수 있는 URL
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


    /*@PutMapping("/modify/{id}")
    public ResponseEntity<Map<String, String>> modify(@PathVariable(name = "id") int id, @RequestBody RulebookDTO rulebookDTO) {
        rulebookDTO.setId(id);
        rulebookService.modifyRulebook(rulebookDTO);
        return ResponseEntity.ok(Map.of("result", "성공"));
    }*/

    @PutMapping("/modify/{id}")
    public ResponseEntity<String> modify(@PathVariable(name = "id") int id, @RequestBody RulebookDTO rulebookDTO) {

        rulebookDTO.setId(id);
        rulebookService.modifyRulebook(rulebookDTO);
        return ResponseEntity.ok("수정 성공");
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

    @PostMapping("/create")
    public Rulebook createRulebook(@RequestBody RulebookDTO rulebookDTO) {
        Rulebook rulebook = new Rulebook();

        // `writerId`를 기반으로 `Gamer` 객체를 찾고, `Rulebook`의 `writer` 필드에 설정
        Gamer writer = gamerRepository.findById(rulebookDTO.getWriterId()).orElseThrow(() -> new RuntimeException("Writer not found"));
        rulebook.setWriter(writer);

        rulebook.setTitle(rulebookDTO.getTitle());
        rulebook.setContent(rulebookDTO.getContent());
        rulebook.setImageUrls(rulebookDTO.getImageUrls());
        rulebook.setYoutubeLinks(rulebookDTO.getYoutubeLinks());

        // 추가적인 처리 로직...
        return rulebookRepository.save(rulebook);  // rulebookRepository.save(rulebook);
    }


    // 추천수 증가
    @PostMapping("/{id}/vote")
    public ResponseEntity<String> incrementVoteCount(
            @PathVariable Integer id,
            @RequestParam Integer gamerId) {

        try {
            rulebookService.incrementVoteCount(id, gamerId);
            return ResponseEntity.ok("추천수가 증가했습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



}