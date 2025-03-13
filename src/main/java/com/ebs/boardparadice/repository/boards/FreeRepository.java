package com.ebs.boardparadice.repository.boards;

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
	
	@EntityGraph(attributePaths = {"imageList"})
	@Query("SELECT f FROM Free f WHERE f.id = :id")
	Optional<Free> selectOne(@Param("id") int id);
	
	@Query("SELECT f, fi, COUNT(a) FROM Free f "
	        + "LEFT JOIN f.imageList fi "
	        + "LEFT JOIN f.answerList a "
	        + "GROUP BY f, fi")
	Page<Object[]> selectList(Pageable pageable);



	
	@Query("SELECT f FROM Free f LEFT JOIN FETCH f.answerList "
			+ "a LEFT JOIN FETCH a.gamer WHERE f.id = :id")
	Optional<Free> findByIdWithAnswers(@Param("id") int id);
}
