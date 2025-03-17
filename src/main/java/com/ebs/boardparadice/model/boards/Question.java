package com.ebs.boardparadice.model.boards;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ebs.boardparadice.model.BoardType;
import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.model.answers.QuestionAnswer;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Builder
@ToString(exclude = "imageList")
@NoArgsConstructor
@AllArgsConstructor
public class Question {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "gamer_id", nullable = false)
    private Gamer gamer;

    @ManyToMany
    private Set<Gamer> voter;

    @Column(name = "createdate", nullable = false, updatable = false)
    private LocalDateTime createdate;
    
    @ElementCollection // ✅ 이미지 리스트 저장
    @CollectionTable(name = "question_image_list", joinColumns = @JoinColumn(name = "question_id"))
    @Builder.Default
    private List<QuestionImage> imageList = new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<QuestionAnswer> answerList = new ArrayList<>();
    
    @Builder.Default
    private BoardType typeId = BoardType.QUESTION;
    
    @Builder.Default
    private int view = 0;

    public void addImageString(String fileName) {
    	QuestionImage questionImage = QuestionImage.builder()
    							.fileName(fileName)
    							.id(this.imageList.size())
    							.build();
    	imageList.add(questionImage);
    }
    
    public void clearList() {
    	this.imageList.clear();
    }
}
