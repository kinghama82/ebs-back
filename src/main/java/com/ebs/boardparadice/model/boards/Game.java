package com.ebs.boardparadice.model.boards;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "gamename", nullable = false, length = 100)
    private String gameName;

    @Column(name = "year", length = 20)
    private String year;

    @Column(name = "players", length = 10)
    private String players;

    @Column(name = "time", length = 10)
    private String time;

    @Column(name = "reage", length = 10)
    private String reage;

    @Column(name = "company", length = 100)
    private String company;

    @Column(name = "scompany", length = 100)
    private String sCompany;

    @Column(name = "price")
    private int price;

    @Column(name = "weight")
    private float weight;

    @Column(name = "engamename", length = 100)
    private String enGameName;

    @Column(name = "bestPlayers",  length = 10)
    private String bestPlayers;

    @Column(name = "avg", nullable = false)
    private float avg;

    private String img;

    @Column(name = "gamerank")
    private int gamerank;

    @ManyToMany
    @JoinTable(
            name = "game_category_mapping", // 중간 테이블 이름
            joinColumns = @JoinColumn(name = "game_id"), // Game 엔티티의 외래 키
            inverseJoinColumns = @JoinColumn(name = "category_id") // GameCategory 엔티티의 외래 키
    )
    // 컬렉션 초기화를 통해 NullPointerException 방지
    private Set<GameCategory> gameCategory = new HashSet<>();

}
