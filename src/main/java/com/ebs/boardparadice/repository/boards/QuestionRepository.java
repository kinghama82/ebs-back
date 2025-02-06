package com.ebs.boardparadice.repository.boards;

import com.ebs.boardparadice.model.boards.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
}
