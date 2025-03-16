package com.ebs.boardparadice.DTO.boards;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.ebs.boardparadice.DTO.answers.AnswerDTO;
import com.ebs.boardparadice.model.Gamer;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RulebookDTO {

    private int id;
    private String title;
    private String content;

    private Gamer writer;
    private int writerId;

    private Set<Gamer> voter;
    private int voteCount;  // 조회수

    private LocalDateTime createdate;
    
    private List<AnswerDTO> answerList;

    // 이미지 URL 리스트
    private List<String> imageUrls;

    // 유튜브 링크 리스트
    private List<String> youtubeLinks;


    @Column(name = "view_count")  // 데이터베이스 컬럼명과 일치시킴
    private int viewCount;
}
