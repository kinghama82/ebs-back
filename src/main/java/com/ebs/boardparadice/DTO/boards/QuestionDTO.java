package com.ebs.boardparadice.DTO.boards;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.ebs.boardparadice.DTO.answers.AnswerDTO;
import com.ebs.boardparadice.model.Gamer;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {

	private int id;
	private String title;
	private String content;
	
	private Gamer writerId;
	private Set<Gamer> voter;
	
	private List<AnswerDTO> answerList;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdate;
	
	
}
