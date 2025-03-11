package com.ebs.boardparadice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "friendship")
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "gamer_id", nullable = false)
    private Gamer gamer;  // 유저 정보 (Gamer 테이블과 연결)

    @ManyToOne
    @JoinColumn(name = "friend_id", nullable = false)
    private Gamer friend;  // 친구 유저 정보 (Gamer 테이블과 연결)
}
