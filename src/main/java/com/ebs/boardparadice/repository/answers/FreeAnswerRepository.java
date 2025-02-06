package com.ebs.boardparadice.repository.answers;


import com.ebs.boardparadice.model.answers.FreeAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FreeAnswerRepository extends JpaRepository<FreeAnswer, Integer> {

}
