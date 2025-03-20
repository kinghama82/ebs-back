package com.ebs.boardparadice.DTO.answers;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RulebookAnswerDTO {
    private int id;
    private String content;
    private int gamer;
    private String writerNickname;
    private int rulebookId; // Rulebook 객체 전체가 아니라 ID만 포함

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
    private int voteCount;
}
