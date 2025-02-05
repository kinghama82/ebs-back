package com.ebs.boardparadice.model.answers;

import com.example.mergeex.model.Free;
import com.example.mergeex.model.Gamer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
public class FreeAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(optional = false)
    @JoinColumn(name = "writer_id")
    private Gamer writerId;

    @ManyToMany
    private Set<Gamer> voter;

    @ManyToOne
    @JoinColumn(name = "free_id")
    private Free freeId;

    @Column(name = "createdate", nullable = false, updatable = false)
    private LocalDate createdate;

    @PrePersist
    public void prePersist() {
        createdate = LocalDate.now();
    }

}
