package com.ebs.boardparadice.repository.answers;

import com.example.mergeex.model.answers.FreeAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreeAnswerRepository extends JpaRepository<FreeAnswer, Integer> {
}
