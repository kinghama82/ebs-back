package com.ebs.boardparadice.repository.answers;


import com.ebs.boardparadice.model.answers.NewsAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsAnswerRepository extends JpaRepository<NewsAnswer, Integer> {
    List<NewsAnswer> findByGamerId(int gamerId);
}
