package com.ebs.boardparadice.model.answers;

import com.ebs.boardparadice.model.BoardType;
import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.model.boards.Rulebook;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class RulebookAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "rulebook_id", nullable = false)
    private Rulebook rulebook;

    @ManyToOne
    @JoinColumn(name = "writer_id", nullable = false)
    private Gamer writer;

    private String content;

    private String nickname;
    
    private BoardType type = BoardType.ANSWERS;

    private LocalDateTime createDate;

    public void prePersist() {
        this.createDate = LocalDateTime.now();
    }
}
