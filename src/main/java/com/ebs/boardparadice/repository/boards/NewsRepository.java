package com.ebs.boardparadice.repository.boards;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ebs.boardparadice.model.boards.News;

@Repository
public interface NewsRepository extends JpaRepository<News, Integer> {
	
	 // 뉴스 게시글 상세 조회 (댓글 포함)
    @Query("SELECT n FROM News n LEFT JOIN FETCH n.answerList a LEFT JOIN FETCH a.gamer WHERE n.id = :id")
    Optional<News> findByIdWithAnswers(@Param("id") int id);
}
