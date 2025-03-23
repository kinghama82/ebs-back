package com.ebs.boardparadice.repository;

import com.ebs.boardparadice.model.Gamer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GamerRepository extends JpaRepository<Gamer, Integer> {

    Optional<Gamer> findByEmail(String email);

    Optional<Gamer> findByNickname(String nickname);

    @EntityGraph(attributePaths = "gamerRoleList", type = EntityGraph.EntityGraphType.LOAD)
    @Query("select g from Gamer g where g.email = :email")
    Optional<Gamer> getWithRoles(@Param("email") String email);

    @Query("SELECT g FROM Gamer g WHERE LOWER(g.nickname) LIKE LOWER(CONCAT('%', :nickname, '%'))")
    List<Gamer> searchByNickname(@Param("nickname") String nickname);

    Optional<Gamer> findByNameAndPhone(String name, String phone);

    // 핸드폰 번호로 조회하는 메서드 추가
    Optional<Gamer> findByPhone(String phone);
}

