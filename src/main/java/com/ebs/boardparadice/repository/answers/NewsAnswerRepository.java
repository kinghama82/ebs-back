package com.ebs.boardparadice.repository.answers;

import com.example.mergeex.model.answers.NewsAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsAnswerRepository extends JpaRepository<NewsAnswer, Integer> {
}
