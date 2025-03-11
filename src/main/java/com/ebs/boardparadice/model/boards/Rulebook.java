package com.ebs.boardparadice.model.boards;

import java.time.LocalDateTime;
import java.util.Set;

import com.ebs.boardparadice.model.BoardType;

import com.ebs.boardparadice.model.Gamer;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

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

    @ManyToOne
    @JoinColumn(name="writer_id")
    private Gamer writer;

    private String nickname;

    @ManyToMany
    private Set<Gamer> voter; 

    private LocalDateTime createdate;

    @Column(length = 255)
    private String imageUrl;

    @ManyToOne
    private BoardType type;

    
    @Column(nullable = false, columnDefinition = "int default 0")
    private int viewCount = 0;  // 조회수, 기본값 0

    
    @PrePersist
    public void prePersist() {
        createdate = LocalDateTime.now();
    }

       // 조회수 증가 메서드
       public void incrementViewCount() {
        this.viewCount++;
       }
    
}
