package com.ebs.boardparadice.repository.boards;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ebs.boardparadice.model.boards.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
	
	//이미지 리스트 포함하는 상세보기
		@EntityGraph(attributePaths = {"imageList"})
		@Query("SELECT DISTINCT q FROM Question q WHERE q.id = :id")
		Optional<Question> selectOne(@Param("id") int id);

		//댓글리스트숫자 포함하는 리스트
		@Query("SELECT q, qi, COUNT(a) FROM Question q "
		        + "LEFT JOIN q.imageList qi "
		        + "LEFT JOIN q.answerList a "
		        + "GROUP BY q, qi")
		Page<Object[]> selectList(Pageable pageable);

		// ✅ `DISTINCT` 적용하여 중복 데이터 방지
			@EntityGraph(attributePaths = {"imageList"})
			@Query("SELECT DISTINCT q FROM Question q ORDER BY q.createdate DESC")
			List<Question> findAllDistinct();



		//댓글리스트 포함 상세보기
		@Query("SELECT q FROM Question q LEFT JOIN FETCH q.answerList "
				+ "a LEFT JOIN FETCH a.gamer WHERE q.id = :id")
		Optional<Question> findByIdWithAnswers(@Param("id") int id);

		//조회수 top5
		@Query("SELECT q FROM Question q WHERE q.view > 0 "
				+ "ORDER BY q.view DESC")
		List<Question> findByViewTop5(Pageable pageable);

		// 추천수 TOP 5 가져오기
		@Query("SELECT q FROM Question q WHERE SIZE(q.voter) > 0 "
		        + "ORDER BY SIZE(q.voter) DESC")
		List<Question> findByVoteTop5(Pageable pageable);
}
