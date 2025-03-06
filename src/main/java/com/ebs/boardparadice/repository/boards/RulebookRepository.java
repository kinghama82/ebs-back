package com.ebs.boardparadice.repository.boards;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ebs.boardparadice.model.boards.Rulebook;

public interface RulebookRepository extends JpaRepository<Rulebook, Integer> {

      // 조회수 증가 메서드
      Rulebook findByIdAndViewCountGreaterThanEqual(int id, int viewCount);
      
}

