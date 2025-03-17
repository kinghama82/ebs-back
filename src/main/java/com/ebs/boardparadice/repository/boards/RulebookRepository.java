package com.ebs.boardparadice.repository.boards;

import java.util.List;
import java.util.Optional;

import com.ebs.boardparadice.model.boards.Free;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ebs.boardparadice.model.boards.Rulebook;

public interface RulebookRepository extends JpaRepository<Rulebook, Integer> {

      // 조회수 증가 메서드
      Rulebook findByIdAndViewCountGreaterThanEqual(int id, int viewCount);

      // 제목에 특정 문자열이 포함된 룰북 찾기 (대소문자 무시)
      List<Rulebook> findByTitleContainingIgnoreCase(String title);
      
      // 룰북 게시글 상세 조회 (댓글 포함)
      @Query("SELECT r FROM Rulebook r LEFT JOIN FETCH r.answerList a LEFT JOIN FETCH a.gamer WHERE r.id = :id")
      Optional<Rulebook> findByIdWithAnswers(@Param("id") int id);

      @Query("SELECT f FROM Free f WHERE f.gamer.id = :gamerId ORDER BY f.createdate DESC")
      List<Free> findByGamerId(@Param("gamerId") int gamerId);
}

