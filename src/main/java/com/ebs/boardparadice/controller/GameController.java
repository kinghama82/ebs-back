package com.ebs.boardparadice.controller;

import com.ebs.boardparadice.requestDTO.GameRequestDTO;
import com.ebs.boardparadice.responseDTO.GameResponseDTO;
import com.ebs.boardparadice.service.GameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final ObjectMapper objectMapper; // JSON 문자열을 DTO로 변환하기 위해

    @Value("${file.upload-dir}")  // 설정 파일에서 값 읽기
    private String uploadDir;

    /**
     * 게임 등록 (파일 업로드 지원)
     * multipart/form-data 형식으로, "game" 파트에는 JSON 문자열, "img" 파트에는 이미지 파일을 포함.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GameResponseDTO> createGame(
            @RequestPart("game") String gameJson,
            @RequestPart(value = "img", required = false) MultipartFile imgFile) {

        try {
            GameRequestDTO gameRequestDTO = objectMapper.readValue(gameJson, GameRequestDTO.class);

            // 파일이 잘 전달되었는지 로그 확인
            if (imgFile != null && !imgFile.isEmpty()) {
                System.out.println("Received file: " + imgFile.getOriginalFilename());
                String imageUrl = saveImageFile(imgFile);
                gameRequestDTO.setImg(imageUrl);
            } else {
                System.out.println("No file received or file is empty");
            }

            GameResponseDTO createdGame = gameService.createGame(gameRequestDTO);
            return new ResponseEntity<>(createdGame, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * 이미지 파일을 로컬에 저장하는 예시 메소드
     * 실제 운영환경에서는 파일명 중복, 경로 보안, 클라우드 스토리지 연동 등을 고려해야 합니다.
     */
    private String saveImageFile(MultipartFile imgFile) throws Exception {
        String extension = imgFile.getOriginalFilename().substring(imgFile.getOriginalFilename().lastIndexOf("."));
        String fileName = System.currentTimeMillis() + extension;

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            System.out.println("✅ Upload directory created: " + uploadPath.toString());
        }

        Path filePath = uploadPath.resolve(fileName);
        System.out.println("🟢 Attempting to save file at: " + filePath.toString());


        try {
            Files.copy(imgFile.getInputStream(), filePath);
            System.out.println("✅ File saved successfully: " + filePath.toString());
        } catch (Exception e) {
            System.err.println("🚨 Error saving file: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return "/uploads/" + fileName;
    }



   /* private String saveImageFile(MultipartFile imgFile) throws Exception {
        String uploadDir = "uploads/";
        String extension = imgFile.getOriginalFilename().substring(imgFile.getOriginalFilename().lastIndexOf("."));
        String fileName = System.currentTimeMillis() + extension; // ✅ 파일명을 숫자로 변환하여 저장
//        String fileName = System.currentTimeMillis() + "_" + imgFile.getOriginalFilename();

        java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir);
        if (!java.nio.file.Files.exists(uploadPath)) {
            java.nio.file.Files.createDirectories(uploadPath);
        }
        java.nio.file.Path filePath = uploadPath.resolve(fileName);
        java.nio.file.Files.copy(imgFile.getInputStream(), filePath);
        return "/uploads/" + fileName; // 클라이언트가 접근 가능한 경로인지 확인
    }*/

    /**
     * 전체 게임 목록 조회 (GET /games)
     */
    @GetMapping
    public ResponseEntity<List<GameResponseDTO>> getAllGames() {
        List<GameResponseDTO> games = gameService.getAllGames();
        return new ResponseEntity<>(games, HttpStatus.OK);
    }

    /**
     * 특정 게임 조회 (GET /games/{id})
     */
    @GetMapping("/{id}")
    public ResponseEntity<GameResponseDTO> getGameById(@PathVariable int id) {
        GameResponseDTO game = gameService.getGameById(id);
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    /**
     * 게임 정보 수정 (PUT /games/{id})
     */
    @PutMapping("/{id}")
    public ResponseEntity<GameResponseDTO> updateGame(@PathVariable int id,
                                                      @RequestBody GameRequestDTO gameRequestDTO) {
        GameResponseDTO updatedGame = gameService.updateGame(id, gameRequestDTO);
        return new ResponseEntity<>(updatedGame, HttpStatus.OK);
    }

    /**
     * 게임 삭제 (DELETE /games/{id})
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable int id) {
        gameService.deleteGame(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
