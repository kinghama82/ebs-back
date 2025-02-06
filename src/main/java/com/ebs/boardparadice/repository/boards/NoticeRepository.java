package com.ebs.boardparadice.repository.boards;


import com.ebs.boardparadice.model.boards.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {
}
