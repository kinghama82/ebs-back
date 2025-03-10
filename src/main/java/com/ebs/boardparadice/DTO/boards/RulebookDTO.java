package com.ebs.boardparadice.DTO.boards;

import com.ebs.boardparadice.model.BoardType;
import com.ebs.boardparadice.model.Gamer;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RulebookDTO {

    private int id;
    private String title;
    private String content;

    private Gamer writerId;

    private Set<Gamer> voter;
    private LocalDateTime createdate;
    private BoardType type;
    private String imageUrl;

    @Column(name = "view_count")  // 데이터베이스 컬럼명과 일치시킴
    private int viewCount;
}
