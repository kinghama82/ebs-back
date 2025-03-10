package com.ebs.boardparadice.repository.boards;

import com.ebs.boardparadice.model.boards.GameCategory;
import com.ebs.boardparadice.model.boards.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GameCategoryRepository extends JpaRepository<GameCategory, Integer> {

    // ✅ 특정 카테고리에 속한 게임 리스트 조회
    @Query("SELECT g FROM Game g JOIN g.gameCategory gc WHERE gc.gameCategory = :category")
    List<Game> findGamesByCategory(@Param("category") String category);
}
