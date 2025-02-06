package com.ebs.boardparadice.repository;

import com.ebs.boardparadice.model.Gamer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GamerRepository extends JpaRepository<Gamer, Integer> {
}
