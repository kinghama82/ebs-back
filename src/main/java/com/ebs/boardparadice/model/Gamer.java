package com.ebs.boardparadice.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(length = 100)
    private String address;

    private boolean social;

    @Column(nullable = false)
    private LocalDateTime createdate;

    @Column(nullable = false, length = 20)
    private String level;

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private List<GamerRole> gamerRoleList = new ArrayList<>();

    public void addRole(GamerRole gamerRole) {
        gamerRoleList.add(gamerRole);
    }

    public void clearRole(){
        gamerRoleList.clear();
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeSocial(boolean social) {
        this.social = social;
    }

    @PrePersist
    public void prePersist() {
        createdate = LocalDateTime.now();
    }
}
