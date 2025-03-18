package com.ebs.boardparadice.DTO.answers;

import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.model.boards.Rulebook;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RulebookAnswerDTO {
    private int id;
    private Rulebook rulebook;
    private Gamer gamer;
    private String content;
    private LocalDateTime createDate;
    private String nickname;

}
