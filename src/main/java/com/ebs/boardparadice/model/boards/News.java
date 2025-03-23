package com.ebs.boardparadice.model.boards;

import com.ebs.boardparadice.model.BoardType;
import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.model.answers.NewsAnswer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@ToString(exclude = "imageList")
@NoArgsConstructor
@AllArgsConstructor
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;
    
    private String category;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "gamer_id", nullable = false)
    private Gamer gamer;

    @ManyToMany
    private Set<Gamer> voter;

    @Column(name = "createdate", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @Builder.Default
    private BoardType typeId = BoardType.NEWS;
    
    @OneToMany(mappedBy = "news", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<NewsAnswer> answerList = new ArrayList<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "news_image_list", joinColumns = @JoinColumn(name = "news_id"))
    private List<NewsImage> imageList = new ArrayList<>();  // ✅ 기본값 설정

    @Builder.Default
    private int view = 0;
    
    public void addImageString(String fileName) {
    	NewsImage newsImage = NewsImage.builder()
    							.fileName(fileName)
    							.id(this.imageList.size())
    							.build();
    	imageList.add(newsImage);
    }
    public void clearList() {
    	this.imageList.clear();
    }
    

    @Column(name = "youtube_url")
    private String youtubeUrl; // ✅ 유튜브 링크 저장 가능

    
}
