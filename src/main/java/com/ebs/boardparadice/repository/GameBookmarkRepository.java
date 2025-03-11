package com.ebs.boardparadice.repository;

import com.ebs.boardparadice.model.GameBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameBookmarkRepository extends JpaRepository<GameBookmark, Integer> {
    List<GameBookmark> findByGamerId(Integer gamerId);
}
