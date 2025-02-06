package com.ebs.boardparadice.model.boards;

import com.ebs.boardparadice.model.BoardType;
import com.ebs.boardparadice.model.Gamer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "writer_id")
    private Gamer writerId;

    @ManyToMany
    private Set<Gamer> voter;

    @Column(name = "createdate", nullable = false, updatable = false)
    private LocalDate createdate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "type_id")
    private BoardType typeId;

    @Column(name = "file_path")
    private String filepath;

    @Column(name = "file_name")
    private String filename;

}
