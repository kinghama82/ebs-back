package com.ebs.boardparadice.repository.boards;

import com.ebs.boardparadice.model.boards.Free;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FreeRepository extends JpaRepository<Free, Integer> {
	
	@EntityGraph(attributePaths = {"imageList"})
	@Query("SELECT f FROM Free f WHERE f.id = :id")
	Optional<Free> selectOne(@Param("id") int id);
	
	@Query("select f, fi from Free f "
			+ "left join f.imageList fi with fi.id = 0 ")
	Page<Object[]> selectList(Pageable pageable);
}
