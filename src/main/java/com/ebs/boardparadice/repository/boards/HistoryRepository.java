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
public interface HistoryRepository extends JpaRepository<History, Integer>{
	
	@Query("SELECT h FROM History h WHERE h.gamer.id = :gamerid")
	Page<History> findByGamerId(@Param("gamerid")Integer gamerid, Pageable pageable);
	
	@Query("SELECT h.game FROM History h "
			+ "WHERE h.gamer.id = :gamerid "
			+ "GROUP BY h.game.id "
			+ "ORDER BY MAX(h.id) DESC")
	List<Game> findRecentGamesByGamerId(@Param("gamerid")Integer gamerid, Pageable pageable);
	
}
