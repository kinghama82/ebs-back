package com.ebs.boardparadice.model;

import com.ebs.boardparadice.model.boards.Game;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "game_bookmark")
public class GameBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "gamer_id", nullable = false)
    private Gamer gamer;  // 유저 정보 (Gamer 테이블과 연결)

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;  // 북마크한 게임 정보 (Game 테이블과 연결)

}
