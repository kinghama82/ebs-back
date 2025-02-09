package com.ebs.boardparadice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "engamename", length = 100)
    private String enGameName;

    @Column(name = "bestPlayers",  length = 10)
    private String bestPlayers;

    @Column(name = "avg", nullable = false)
    private float avg;

    private String img;

    @Column(name = "gamerank")
    private int gamerank;

}
