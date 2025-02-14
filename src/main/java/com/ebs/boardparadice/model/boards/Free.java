package com.ebs.boardparadice.model.boards;

import com.ebs.boardparadice.model.BoardType;
import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.model.answers.FreeAnswer;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Free {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "writer_id", nullable = false)
    private Gamer writerId;

    @ManyToMany
    private Set<Gamer> voter;

    @Column(name = "createdate", nullable = false, updatable = false)
    private LocalDate createdate;

    @OneToMany(mappedBy = "freeId", cascade = CascadeType.REMOVE)
    private List<FreeAnswer> answerList;
    
    @ManyToOne
    @JoinColumn(name = "type_id")
    private BoardType typeId;

    @Column(name = "file_path")
    private String filepath;

    @Column(name = "file_name")
    private String filename;

}
