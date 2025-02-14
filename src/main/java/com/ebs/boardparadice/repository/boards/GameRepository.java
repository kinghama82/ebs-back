package com.ebs.boardparadice.repository.boards;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ebs.boardparadice.model.boards.Game;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {
}
