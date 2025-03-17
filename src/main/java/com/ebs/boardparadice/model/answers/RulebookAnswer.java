package com.ebs.boardparadice.model.answers;

import java.time.LocalDateTime;
import java.util.Set;

import com.ebs.boardparadice.model.BoardType;
import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.model.boards.Rulebook;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RulebookAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @ManyToOne
    @JoinColumn(name = "gamer_id", nullable = false)
    private Gamer gamer;
    
    @ManyToMany
    private Set<Gamer> voter;
    
    @ManyToOne
    @JoinColumn(name = "rulebook_id", nullable = false)
    private Rulebook rulebook;

    @Column(name = "createdate", nullable = false, updatable = false)
    private LocalDateTime createdate;
    
    @Builder.Default
    private BoardType type = BoardType.ANSWERS;

    @PrePersist
    public void prePersist() {
    	if(createdate == null) {
    		createdate = LocalDateTime.now();
    	}
    }
}
