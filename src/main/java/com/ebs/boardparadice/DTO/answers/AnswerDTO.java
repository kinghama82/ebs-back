package com.ebs.boardparadice.DTO.answers;

import java.time.LocalDateTime;
import java.util.Set;

import com.ebs.boardparadice.model.Gamer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerDTO {

	private int id;
	private String content;
	
	@JsonProperty("gamer")
	private Gamer gamer;
	
	@JsonProperty("free")
	private int free;
	@JsonProperty("question")
	private int question;
	/*@JsonProperty("rulebook")
	private int rulebook;*/
	@JsonProperty("news")
	private int news;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdate;
	
	private Set<Gamer> voter;
	
	
}
