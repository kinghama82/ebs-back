package com.ebs.boardparadice.model.boards;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ebs.boardparadice.model.BoardType;
import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.model.answers.FreeAnswer;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
public class Free {

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
    
    @ElementCollection
    @Builder.Default
    private List<FreeImage> imageList = new ArrayList<>();

    @OneToMany(mappedBy = "free", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<FreeAnswer> answerList = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name = "type_id")
    private BoardType typeId;

    public void addImageString(String fileName) {
    	FreeImage freeImage = FreeImage.builder()
    							.fileName(fileName)
    							.id(this.imageList.size())
    							.build();
    	imageList.add(freeImage);
    }
    
    public void clearList() {
    	this.imageList.clear();
    }

}
