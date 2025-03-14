package com.ebs.boardparadice.model.boards;

import com.ebs.boardparadice.model.BoardType;
import com.ebs.boardparadice.model.Gamer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(optional = false)
    @JoinColumn(name = "writer_id")
    private Gamer writerId;

    @ManyToMany
    private Set<Gamer> voter;

    @Column(name = "createdate", nullable = false, updatable = false)
    private LocalDate createdate;

    private BoardType typeId = BoardType.NEWS;


    @ElementCollection
    private List<String> imageUrls = new ArrayList<>();  // ✅ 기본값 설정


    @Column(name = "youtube_url")
    private String youtubeUrl; // ✅ 유튜브 링크 저장 가능

    @PrePersist
    public void prePersist() {
        this.createdate = LocalDate.now();
    }
}
