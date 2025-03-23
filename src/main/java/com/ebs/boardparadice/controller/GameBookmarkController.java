package com.ebs.boardparadice.controller;

import com.ebs.boardparadice.DTO.GameBookmarkDTO;
import com.ebs.boardparadice.DTO.GameBookmarkRequestDTO;
import com.ebs.boardparadice.service.GameBookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class GameBookmarkController {

    private final GameBookmarkService gameBookmarkService;

    // ✅ 북마크 추가 (DTO 사용)
    @PostMapping
    public ResponseEntity<GameBookmarkDTO> addBookmark(@RequestBody GameBookmarkRequestDTO requestDTO) {
        GameBookmarkDTO savedBookmark = gameBookmarkService.addBookmark(requestDTO.getGamerId(), requestDTO.getGameId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBookmark);
    }

    // ✅ 북마크 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeBookmark(@PathVariable(name = "id") Integer id) {
        gameBookmarkService.removeBookmark(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ 특정 사용자의 게임 북마크 목록 조회
    @GetMapping("/{gamerId}")
    public ResponseEntity<List<GameBookmarkDTO>> getBookmarks(@PathVariable(name = "gamerId") Integer gamerId) {
        List<GameBookmarkDTO> bookmarks = gameBookmarkService.getBookmarksByGamerId(gamerId);
        
        return ResponseEntity.ok(bookmarks);
    }
}
