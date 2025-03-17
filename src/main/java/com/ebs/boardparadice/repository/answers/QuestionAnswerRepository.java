package com.ebs.boardparadice.repository.answers;


import com.ebs.boardparadice.model.answers.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Integer> {
    List<QuestionAnswer> findByGamerId(int gamerId);
}
