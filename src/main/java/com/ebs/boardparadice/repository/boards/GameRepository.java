package com.ebs.boardparadice.repository.boards;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ebs.boardparadice.model.boards.Game;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {

    @EntityGraph(attributePaths = {"gameCategory"})  // gameCategory를 한 번에 조회
    List<Game> findAll();

    // ✅ 게임 이름, 회사, 영어 이름 등을 검색할 수 있는 쿼리 메서드 추가
    List<Game> findByGameNameContainingOrCompanyContainingOrEnGameNameContaining(
            String gameName, String company, String enGameName
    );
}
