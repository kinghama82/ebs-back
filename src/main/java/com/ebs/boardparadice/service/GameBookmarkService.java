package com.ebs.boardparadice.service;

import com.ebs.boardparadice.DTO.GameBookmarkDTO;
import com.ebs.boardparadice.model.GameBookmark;
import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.model.boards.Game;
import com.ebs.boardparadice.repository.GameBookmarkRepository;
import com.ebs.boardparadice.repository.GamerRepository;
import com.ebs.boardparadice.repository.boards.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameBookmarkService {

    private final GameBookmarkRepository gameBookmarkRepository;
    private final GamerRepository gamerRepository;
    private final GameRepository gameRepository;

    // ✅ 게임 북마크 조회
    public List<GameBookmarkDTO> getBookmarksByGamerId(Integer gamerId) {
        List<GameBookmark> bookmarks = gameBookmarkRepository.findByGamerId(gamerId);
        return bookmarks.stream()
                .map(GameBookmarkDTO::new) // ✅ DTO 변환 시 게임 정보 포함
                .collect(Collectors.toList());
    }

    // ✅ 북마크 추가 (DTO를 반환하도록 수정)
    public GameBookmarkDTO addBookmark(Integer gamerId, Integer gameId) {
        Gamer gamer = gamerRepository.findById(gamerId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("게임을 찾을 수 없습니다."));

        GameBookmark bookmark = new GameBookmark();
        bookmark.setGamer(gamer);
        bookmark.setGame(game);

        GameBookmark savedBookmark = gameBookmarkRepository.save(bookmark);
        return new GameBookmarkDTO(savedBookmark); // ✅ DTO로 변환하여 반환
    }

    // ✅ 북마크 삭제
    public void removeBookmark(Integer id) {
        gameBookmarkRepository.deleteById(id);
    }

    // ✅ 특정 게임 조회
    public Game getGameById(Integer id) {
        return gameRepository.findById(id).orElse(null);
    }
}
