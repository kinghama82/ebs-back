package com.ebs.boardparadice.repository.answers;


import com.ebs.boardparadice.model.answers.NoticeAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeAnswerRepository extends JpaRepository<NoticeAnswer, Integer> {
}
