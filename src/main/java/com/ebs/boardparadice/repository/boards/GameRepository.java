package com.ebs.boardparadice.repository.boards;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ebs.boardparadice.model.boards.Game;

import java.util.List;


@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {

    @EntityGraph(attributePaths = {"gameCategory"})  // gameCategory를 한 번에 조회
    List<Game> findAll();

    /*// ✅ 여러 개의 검색 결과를 반환하도록 유지
    List<Game> findByGameNameContainingOrCompanyContainingOrEnGameNameContaining(
            String gameName, String company, String enGameName
    );*/

    @Query("SELECT g FROM Game g WHERE LOWER(g.gameName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(g.company) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(g.enGameName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Game> searchGames(@Param("keyword") String keyword);

}

