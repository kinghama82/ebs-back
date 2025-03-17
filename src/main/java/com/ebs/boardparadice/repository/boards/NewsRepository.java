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

import com.ebs.boardparadice.model.boards.News;

@Repository
public interface NewsRepository extends JpaRepository<News, Integer> {

	// 이미지 리스트 포함하는 상세보기
	@EntityGraph(attributePaths = { "imageList" })
	@Query("SELECT DISTINCT n FROM News n WHERE n.id = :id")
	Optional<News> selectOne(@Param("id") int id);

	// 댓글리스트숫자 포함하는 리스트
	@Query("SELECT n, ni, COUNT(a) FROM News n " 
			+ "LEFT JOIN n.imageList ni " 
			+ "LEFT JOIN n.answerList a "
			+ "GROUP BY n, ni")
	Page<Object[]> selectList(Pageable pageable);

	// ✅ `DISTINCT` 적용하여 중복 데이터 방지
	@EntityGraph(attributePaths = { "imageList" })
	@Query("SELECT DISTINCT n FROM News n ORDER BY n.createdate DESC")
	List<News> findAllDistinct();

	// 댓글리스트 포함 상세보기
	@Query("SELECT n FROM News n LEFT JOIN FETCH n.answerList " 
			+ "a LEFT JOIN FETCH a.gamer WHERE n.id = :id")
	Optional<News> findByIdWithAnswers(@Param("id") int id);

	// 조회수 top5
	@Query("SELECT n FROM News n WHERE n.view > 0 " 
			+ "ORDER BY n.view DESC")
	List<News> findByViewTop5(Pageable pageable);

	// 추천수 TOP 5 가져오기
	@Query("SELECT n FROM News n WHERE SIZE(n.voter) > 0 " 
			+ "ORDER BY SIZE(n.voter) DESC")
	List<News> findByVoteTop5(Pageable pageable);
}
