package com.ebs.boardparadice.repository.answers;


import com.ebs.boardparadice.model.answers.FreeAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FreeAnswerRepository extends JpaRepository<FreeAnswer, Integer> {
    List<FreeAnswer> findByFreeId(Integer freeId);
}
