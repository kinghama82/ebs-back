package com.ebs.boardparadice.model.answers;


import com.ebs.boardparadice.model.boards.Free;
import com.ebs.boardparadice.model.BoardType;
import com.ebs.boardparadice.model.Gamer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreeAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "gamer_id")
    private Gamer gamer;

    @ManyToMany
    private Set<Gamer> voter;

    @ManyToOne
    @JoinColumn(name = "free_id", nullable = false)
    private Free free;

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
