package com.ebs.boardparadice.repository;

import com.example.mergeex.model.Gamer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GamerRepository extends JpaRepository<Gamer, Integer> {
}
