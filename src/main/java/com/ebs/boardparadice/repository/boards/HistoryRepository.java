package com.ebs.boardparadice.repository.boards;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ebs.boardparadice.model.boards.History;

@Repository
public interface HistoryRepository extends JpaRepository<History, Integer>{

}
