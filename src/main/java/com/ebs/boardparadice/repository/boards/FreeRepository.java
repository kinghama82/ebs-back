package com.ebs.boardparadice.repository.boards;

import com.ebs.boardparadice.model.boards.Free;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreeRepository extends JpaRepository<Free, Integer> {
}
