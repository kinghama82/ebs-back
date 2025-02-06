package com.ebs.boardparadice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
public class Rulebook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(optional = false)
    private Gamer writerId;

    @ManyToMany
    private Set<Gamer> voter;

    private LocalDateTime createdate;

//    private game gameid

    @ManyToOne
    private BoardType type;

    @PrePersist
    public void prePersist() {
        createdate = LocalDateTime.now();
    }

}
