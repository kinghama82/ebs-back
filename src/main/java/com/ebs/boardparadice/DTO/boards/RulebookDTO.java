package com.ebs.boardparadice.DTO.boards;

import com.ebs.boardparadice.model.BoardType;
import com.ebs.boardparadice.model.Gamer;
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
    private String writer_id;
    private Set<Gamer> voter;
    private LocalDateTime createdate;
    private BoardType type;


}
