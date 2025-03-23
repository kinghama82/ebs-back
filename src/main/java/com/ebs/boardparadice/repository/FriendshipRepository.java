package com.ebs.boardparadice.repository;

import com.ebs.boardparadice.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {
    List<Friendship> findByGamerId(Integer gamerId);
}
