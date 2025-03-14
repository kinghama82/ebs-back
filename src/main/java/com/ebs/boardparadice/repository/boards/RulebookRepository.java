package com.ebs.boardparadice.repository.boards;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ebs.boardparadice.model.boards.Rulebook;

import java.util.List;

public interface RulebookRepository extends JpaRepository<Rulebook, Integer> {

      // 조회수 증가 메서드
      Rulebook findByIdAndViewCountGreaterThanEqual(int id, int viewCount);

      // 제목에 특정 문자열이 포함된 룰북 찾기 (대소문자 무시)
      List<Rulebook> findByTitleContainingIgnoreCase(String title);
      
}

