package com.ebs.boardparadice.repository.boards;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ebs.boardparadice.model.boards.Game;
import com.ebs.boardparadice.model.boards.History;

@Repository
public interface HistoryRepository extends JpaRepository<History, Integer> {

	@Query("SELECT h FROM History h WHERE h.gamer.id = :gamerid")
	Page<History> findByGamerId(@Param("gamerid") Integer gamerid, Pageable pageable);

	// 작성자id를 가지고 최신 플레이 게임 검색
	@Query("SELECT h.game FROM History h " + "WHERE h.gamer.id = :gamerid " + "GROUP BY h.game.id "
			+ "ORDER BY MAX(h.id) DESC")
	List<Game> findRecentGamesByGamerId(@Param("gamerid") Integer gamerid, Pageable pageable);

	// 작성자id와 연도를 가지고 해당 연도의 모든 히스토리 검색
	@Query("SELECT h FROM History h WHERE h.gamer.id = :gamerid " + " AND FUNCTION('YEAR', h.date) = :year")
	Page<History> findAllByGamerIdAndYear(@Param("gamerid") Integer gamerid, @Param("year") Integer year,
			Pageable pageable);

	// 전체기록 승무패 횟수세기
	@Query("SELECT " 
			+ "COUNT(CASE WHEN h.win = 1 THEN 1 ELSE NULL END), "
			+ "COUNT(CASE WHEN h.draw = 1 THEN 1 ELSE NULL END), " 
			+ "COUNT(CASE WHEN h.lose = 1 THEN 1 ELSE NULL END) "
			+ "FROM History h WHERE h.gamer.id = :gamerid")
	List<Object[]> countWinDrawLoseByGamerId(@Param("gamerid") Integer gamerid);
	
}
