package com.ebs.boardparadice.repository;

import com.ebs.boardparadice.model.Gamer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GamerRepository extends JpaRepository<Gamer, Integer> {
    Optional<Gamer> findByEmail(String email);

    @EntityGraph(attributePaths = "memberRoleList")
    @Query("select m from Gamer m where m.email = :email")
    Gamer getWithRoles(@Param("email") String email);

}
