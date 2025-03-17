package com.ebs.boardparadice.repository.boards;

import java.util.List;
import java.util.Optional;

import com.ebs.boardparadice.model.boards.Free;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ebs.boardparadice.model.boards.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
	
	// 질문 게시글 상세 조회 (댓글 포함)
	@EntityGraph(attributePaths = {"answerList", "answerList.gamer"})
    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.answerList a LEFT JOIN FETCH a.gamer WHERE q.id = :id")
    Optional<Question> findByIdWithAnswers(@Param("id") int id);

    @Query("SELECT f FROM Free f WHERE f.gamer.id = :gamerId ORDER BY f.createdate DESC")
    List<Free> findByGamerId(@Param("gamerId") int gamerId);
}
