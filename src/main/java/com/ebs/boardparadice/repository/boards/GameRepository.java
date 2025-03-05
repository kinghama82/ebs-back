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
}
