package com.ebs.boardparadice.model.answers;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.ebs.boardparadice.DTO.answers.RulebookAnswerDTO;
import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.model.boards.Rulebook;
import com.fasterxml.jackson.annotation.JsonIgnore; // JSON 변환 시 무한 참조 방지

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "gamer_id", nullable = false)
    private Gamer gamer;

    @ManyToOne
    @JoinColumn(name = "rulebook_id", nullable = false)
    @JsonIgnore // 무한 루프 방지
    private Rulebook rulebook;

    @ManyToMany
    private Set<Gamer> voters = new HashSet<>();

    @Column( updatable = false)
    private LocalDateTime createdDate;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
    }

    public RulebookAnswerDTO toDTO() {
        return RulebookAnswerDTO.builder()
                .id(this.id)
                .content(this.content)
                .gamer(this.gamer.getId())
                .writerNickname(this.gamer.getNickname())
                .rulebookId(this.rulebook != null ? this.rulebook.getId() : 0) // Null 방지 처리
                .createdDate(this.createdDate)
                /*.voteCount(this.voters.size())*/
                .build();
    }
}
