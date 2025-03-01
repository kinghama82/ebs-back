package com.ebs.boardparadice.repository.boards;

import com.ebs.boardparadice.model.boards.GameCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameCategoryRepository extends JpaRepository<GameCategory, Integer> {
}
