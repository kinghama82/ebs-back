package com.ebs.boardparadice.repository.answers;


import com.ebs.boardparadice.model.answers.RulebookAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RulebookAnswerRepository extends JpaRepository<RulebookAnswer, Integer> {

    List<RulebookAnswer> findByRulebookId(int rulebookId);
}
