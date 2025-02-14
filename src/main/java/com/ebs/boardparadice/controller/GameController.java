package com.ebs.boardparadice.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ebs.boardparadice.DTO.boards.GameDTO;
import com.ebs.boardparadice.service.boards.GameService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final ObjectMapper objectMapper; // JSON 문자열을 DTO로 변환하기 위해

    /**
     * 게임 등록 (파일 업로드 지원)
     * multipart/form-data 형식으로, "game" 파트에는 JSON 문자열, "img" 파트에는 이미지 파일을 포함.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GameDTO> createGame(
            @RequestPart("game") String gameJson,  // JSON 형식의 게임 데이터를 문자열로 받음
            @RequestPart(value = "img", required = false) MultipartFile imgFile) { // 이미지 파일을 선택적으로 받음

        try {
            // JSON 데이터를 GameRequestDTO 객체로 변환
            GameDTO gameDTO = objectMapper.readValue(gameJson, GameDTO.class);

            // 업로드된 파일이 존재하는지 확인 후 처리
            if (imgFile != null && !imgFile.isEmpty()) {
                System.out.println("📂 받은 파일: " + imgFile.getOriginalFilename()); // 파일명 출력
                String imageUrl = saveImageFile(imgFile); // 파일 저장 후 URL 반환
                gameDTO.setImg(imageUrl); // DTO에 이미지 URL 설정
            } else {
                System.out.println("⚠️ 파일이 없거나 비어 있음"); // 파일이 없을 경우 로그 출력
            }

            // 게임 생성 서비스 호출
            GameDTO createdGame = gameService.createGame(gameDTO);

            // 생성된 게임 정보를 HTTP 상태 201(CREATED)와 함께 반환
            return new ResponseEntity<>(createdGame, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();

            // 예외 발생 시 400(BAD_REQUEST) 응답 반환
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 이미지 파일을 로컬에 저장하는 예시 메소드
     * 실제 운영환경에서는 파일명 중복, 경로 보안, 클라우드 스토리지 연동 등을 고려해야 합니다.
     */
    private String saveImageFile(MultipartFile imgFile) throws Exception {
        // ✅ 프로젝트 루트 경로 기준으로 static/uploads 폴더 설정
        String projectDir = System.getProperty("user.dir");
        Path uploadPath = Paths.get(projectDir, "src", "main", "resources", "static", "uploads");

        // ✅ uploads 폴더가 없으면 생성
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            System.out.println("✅ 업로드 디렉토리 생성됨: " + uploadPath.toString());
        }

        // ✅ UUID를 사용하여 고유한 파일 이름 생성
        String extension = imgFile.getOriginalFilename().substring(imgFile.getOriginalFilename().lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;

        Path filePath = uploadPath.resolve(fileName);
        System.out.println("🟢 파일 저장 시도 중: " + filePath.toString());

        try {
            Files.copy(imgFile.getInputStream(), filePath);
            System.out.println("✅ 파일 저장 완료: " + filePath.toString());
        } catch (Exception e) {
            System.err.println("🚨 파일 저장 오류: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        // ✅ 저장된 파일의 경로를 반환 (정적 리소스 경로로)
        return "/uploads/" + fileName;
    }


    /**
     * 전체 게임 목록 조회 (GET /games)
     */
    @GetMapping
    public ResponseEntity<List<GameDTO>> getAllGames() {
        List<GameDTO> games = gameService.getAllGames();
        return new ResponseEntity<>(games, HttpStatus.OK);
    }

    /**
     * 특정 게임 조회 (GET /games/{id})
     */
    @GetMapping("/{id}")
    public ResponseEntity<GameDTO> getGameById(@PathVariable int id) {
        GameDTO game = gameService.getGameById(id);
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    /**
     * 게임 정보 수정 (PUT /games/{id})
     */
    @PutMapping("/{id}")
    public ResponseEntity<GameDTO> updateGame(@PathVariable int id,
                                                      @RequestBody GameDTO gameDTO) {
        GameDTO updatedGame = gameService.updateGame(id, gameDTO);
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
