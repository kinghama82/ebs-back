package com.ebs.boardparadice.service.boards;

import java.util.List;
import java.util.stream.Collectors;

import com.ebs.boardparadice.DTO.boards.GameCategoryDTO;
import com.ebs.boardparadice.model.boards.GameCategory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ebs.boardparadice.DTO.boards.GameDTO;
import com.ebs.boardparadice.model.boards.Game;
import com.ebs.boardparadice.repository.boards.GameRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    /**
     * 새로운 게임 등록
     */
    @Transactional
    public GameDTO createGame(GameDTO gameDTO) {
        Game game = new Game();

        // DTO에서 엔티티로 값 복사
        game.setGameName(gameDTO.getGameName());
        game.setYear(gameDTO.getYear());
        game.setPlayers(gameDTO.getPlayers());
        game.setTime(gameDTO.getTime());
        game.setReage(gameDTO.getReage());
        game.setCompany(gameDTO.getCompany());
        game.setSCompany(gameDTO.getSCompany());
        game.setPrice(gameDTO.getPrice());
        game.setEnGameName(gameDTO.getEnGameName());
        game.setBestPlayers(gameDTO.getBestPlayers());
        game.setAvg(gameDTO.getAvg());
        game.setGamerank(gameDTO.getGamerank());

        // ✅ img 필드가 null이 아니면 설정
        if (gameDTO.getImg() != null) {
            game.setImg(gameDTO.getImg());
        }

        Game savedGame = gameRepository.save(game);
        return convertToDTO(savedGame);
    }

    /**
     * 전체 게임 목록 조회
     */
/*    @Transactional   //lazy문제 해결을 위한 어노테이션
    public List<GameDTO> getAllGames() {
        List<Game> games = gameRepository.findAll();
        return games.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }*/

    @Transactional
    public List<GameDTO> getAllGames() {
        List<Game> games = gameRepository.findAll();  // @EntityGraph 덕분에 JOIN이 수행됨
        return games.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    /**
     * 특정 게임 조회 (ID 기준)
     */
    @Transactional
    public GameDTO getGameById(int id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게임을 찾을수 없습니다 id: " + id));
        return convertToDTO(game);
    }

    /**
     * 게임 정보 수정
     */
    @Transactional
    public GameDTO updateGame(int id, GameDTO gameDTO) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게임을 찾을수 없습니다 id: " + id));

        // 필요한 필드 업데이트
        game.setGameName(gameDTO.getGameName());
        game.setYear(gameDTO.getYear());
        game.setPlayers(gameDTO.getPlayers());
        game.setTime(gameDTO.getTime());
        game.setReage(gameDTO.getReage());
        game.setCompany(gameDTO.getCompany());
        game.setSCompany(gameDTO.getSCompany());
        game.setPrice(gameDTO.getPrice());
        game.setEnGameName(gameDTO.getEnGameName());
        game.setBestPlayers(gameDTO.getBestPlayers());
        game.setAvg(gameDTO.getAvg());
        game.setGamerank(gameDTO.getGamerank());

        // @Transactional 어노테이션으로 인하여 별도의 save() 호출 없이 변경사항이 반영됨
        return convertToDTO(game);
    }

    /**
     * 게임 삭제
     */
    @Transactional
    public void deleteGame(int id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + id));
        gameRepository.delete(game);
    }

    /**
     * Game 엔티티를 GameResponseDTO로 변환
     */

    private GameDTO convertToDTO(Game game) {
        return GameDTO.builder()
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
                .img((game.getImg() != null) ? game.getImg() : "")
                .gameCategory(game.getGameCategory().stream()
                        .map(category -> GameCategoryDTO.builder()
                                .id(category.getId())
                                .gameCategory(category.getGameCategory()) // 카테고리 이름
                                .description(category.getDescription())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }

    @Transactional
    public void addCategory(int gameId, GameCategory category) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("게임을 찾을수 없습니다 id: " + gameId));
        game.getGameCategory().add(category);
        gameRepository.save(game);
    }

    @Transactional
    public List<GameDTO> searchGames(String keyword) {
        List<Game> games = gameRepository.searchGames(
                keyword
        );
/*
        // ✅ 검색된 게임 개수 및 내용 출력
        System.out.println("🔍 검색된 게임 개수: " + games.size());
        for (Game game : games) {
            System.out.println("게임 이름: " + game.getGameName());
        }*/

        return games.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


}
