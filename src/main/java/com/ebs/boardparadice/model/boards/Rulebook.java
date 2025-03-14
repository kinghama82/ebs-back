package com.ebs.boardparadice.model.boards;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ebs.boardparadice.model.BoardType;

import com.ebs.boardparadice.model.Gamer;

import jakarta.persistence.*;
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

    @ManyToMany
    private Set<Gamer> voter;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int voteCount = 0;  // 추천수 필드

    private LocalDateTime createdate;

    private BoardType type = BoardType.RULEBOOK;

    
    @Column(nullable = false, columnDefinition = "int default 0")
    private int viewCount = 0;  // 조회수, 기본값 0

    @ElementCollection
    @CollectionTable(name = "rulebook_images", joinColumns = @JoinColumn(name = "rulebook_id"))
    private List<String> imageUrls = new ArrayList<>();  // 이미지 URL을 저장할 리스트

    // ✅ 여러 개의 유튜브 링크 저장
    @ElementCollection
    @CollectionTable(name = "rulebook_youtube_links", joinColumns = @JoinColumn(name = "rulebook_id"))
    private List<String> youtubeLinks;

    
    @PrePersist
    public void prePersist() {
        createdate = LocalDateTime.now();
    }

    public void addImageUrl(String imageUrl) {
        this.imageUrls.add(imageUrl);
    }

       // 조회수 증가 메서드
       public void incrementViewCount() {
        this.viewCount++;
       }

    // 추천수 증가 메서드
    public void incrementVoteCount() {
        this.voteCount++;
    }

    // 추천 유저 추가 메서드
    public void addVoter(Gamer gamer) {
        this.voter.add(gamer);
        incrementVoteCount();  // 유저 추가 시 추천수 증가
    }

}
