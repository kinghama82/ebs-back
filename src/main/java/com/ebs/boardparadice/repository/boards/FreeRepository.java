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

import com.ebs.boardparadice.model.boards.Free;

@Repository
public interface FreeRepository extends JpaRepository<Free, Integer> {
	
	//이미지 리스트 포함하는 상세보기
	@EntityGraph(attributePaths = {"imageList"})
	@Query("SELECT DISTINCT f FROM Free f WHERE f.id = :id")
	Optional<Free> selectOne(@Param("id") int id);
	
	//댓글리스트숫자 포함하는 리스트
	@Query("SELECT f, fi, COUNT(a) FROM Free f "
	        + "LEFT JOIN f.imageList fi "
	        + "LEFT JOIN f.answerList a "
	        + "GROUP BY f, fi")
	Page<Object[]> selectList(Pageable pageable);

	// ✅ `DISTINCT` 적용하여 중복 데이터 방지
		@EntityGraph(attributePaths = {"imageList"})
		@Query("SELECT DISTINCT f FROM Free f ORDER BY f.createdate DESC")
		List<Free> findAllDistinct();
	


	//댓글리스트 포함 상세보기
	@Query("SELECT f FROM Free f LEFT JOIN FETCH f.answerList "
			+ "a LEFT JOIN FETCH a.gamer WHERE f.id = :id")
	Optional<Free> findByIdWithAnswers(@Param("id") int id);
	
	//조회수 top5
	@Query("SELECT f FROM Free f WHERE f.view > 0 "
			+ "ORDER BY f.view DESC")
	List<Free> findByViewTop5(Pageable pageable);
	
	// 추천수 TOP 5 가져오기
	@Query("SELECT f FROM Free f WHERE SIZE(f.voter) > 0 "
	        + "ORDER BY SIZE(f.voter) DESC")
	List<Free> findByVoteTop5(Pageable pageable);

	
	
}
