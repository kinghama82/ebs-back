package com.ebs.boardparadice.repository;

import com.ebs.boardparadice.model.BoardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardTypeRepository extends JpaRepository<BoardType, Integer> {
}
