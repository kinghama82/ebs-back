package com.ebs.boardparadice.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Gamer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 20)
    private String nickname;

    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @Column(nullable = false, length = 100)
    private String address;

    @Column(nullable = false, length = 10)
    private String role;

    @Column(nullable = false)
    private LocalDateTime createdate;

    @Column(nullable = false, length = 20)
    private String level;

    @PrePersist
    public void prePersist() {
        createdate = LocalDateTime.now();
    }
}
