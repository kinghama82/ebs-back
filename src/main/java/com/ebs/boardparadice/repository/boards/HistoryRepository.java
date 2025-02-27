package com.ebs.boardparadice.repository.boards;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ebs.boardparadice.model.boards.History;

public interface HistoryRepository extends JpaRepository<History, Integer>{

}
