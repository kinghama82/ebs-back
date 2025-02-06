package com.ebs.boardparadice.model.answers;


import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.model.Notice;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
public class NoticeAnswer {

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
    @JoinColumn(name = "notice_id")
    private Notice noticeId;

    @Column(name = "createdate", nullable = false, updatable = false)
    private LocalDate createdate;

    @PrePersist
    public void prePersist() {
        createdate = LocalDate.now();
    }
}
