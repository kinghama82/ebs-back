package com.ebs.boardparadice.service;

import com.ebs.boardparadice.model.Game;
import com.ebs.boardparadice.repository.GameRepository;
import com.ebs.boardparadice.requestDTO.GameRequestDTO;
import com.ebs.boardparadice.responseDTO.GameResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    /**
     * 새로운 게임 등록
     */
    public GameResponseDTO createGame(GameRequestDTO gameRequestDTO) {
        Game game = new Game();

        // DTO에서 엔티티로 값 복사
        game.setGameName(gameRequestDTO.getGameName());
        game.setYear(gameRequestDTO.getYear());
        game.setPlayers(gameRequestDTO.getPlayers());
        game.setTime(gameRequestDTO.getTime());
        game.setReage(gameRequestDTO.getReage());
        game.setCompany(gameRequestDTO.getCompany());
        game.setSCompany(gameRequestDTO.getSCompany());
        game.setPrice(gameRequestDTO.getPrice());
        game.setEnGameName(gameRequestDTO.getEnGameName());
        game.setBestPlayers(gameRequestDTO.getBestPlayers());
        game.setAvg(gameRequestDTO.getAvg());
        game.setGamerank(gameRequestDTO.getGamerank());

        // ✅ img 필드가 null이 아니면 설정
        if (gameRequestDTO.getImg() != null) {
            game.setImg(gameRequestDTO.getImg());
        }

        Game savedGame = gameRepository.save(game);
        return convertToDTO(savedGame);
    }

    /**
     * 전체 게임 목록 조회
     */
    public List<GameResponseDTO> getAllGames() {
        List<Game> games = gameRepository.findAll();
        return games.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 특정 게임 조회 (ID 기준)
     */
    public GameResponseDTO getGameById(int id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + id));
        return convertToDTO(game);
    }

    /**
     * 게임 정보 수정
     */
    @Transactional
    public GameResponseDTO updateGame(int id, GameRequestDTO gameRequestDTO) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + id));

        // 필요한 필드 업데이트
        game.setGameName(gameRequestDTO.getGameName());
        game.setYear(gameRequestDTO.getYear());
        game.setPlayers(gameRequestDTO.getPlayers());
        game.setTime(gameRequestDTO.getTime());
        game.setReage(gameRequestDTO.getReage());
        game.setCompany(gameRequestDTO.getCompany());
        game.setSCompany(gameRequestDTO.getSCompany());
        game.setPrice(gameRequestDTO.getPrice());
        game.setEnGameName(gameRequestDTO.getEnGameName());
        game.setBestPlayers(gameRequestDTO.getBestPlayers());
        game.setAvg(gameRequestDTO.getAvg());
        game.setGamerank(gameRequestDTO.getGamerank());

        // @Transactional 어노테이션으로 인하여 별도의 save() 호출 없이 변경사항이 반영됨
        return convertToDTO(game);
    }

    /**
     * 게임 삭제
     */
    public void deleteGame(int id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + id));
        gameRepository.delete(game);
    }

    /**
     * Game 엔티티를 GameResponseDTO로 변환
     */
    private GameResponseDTO convertToDTO(Game game) {
        return GameResponseDTO.builder()
                .id(game.getId())
                .gameName(game.getGameName())
                .year(game.getYear())
                .players(game.getPlayers())
                .time(game.getTime())
                .reage(game.getReage())
                .company(game.getCompany())
                .sCompany(game.getSCompany())
                .price(game.getPrice())
                .enGameName(game.getEnGameName())
                .bestPlayers(game.getBestPlayers())
                .avg(game.getAvg())
                .gamerank(game.getGamerank())
                .img((game.getImg() != null) ? game.getImg() : "") // ✅ 이미지가 null이면 빈 값 반환
                .build();
    }
}
