package com.ebs.boardparadice.DTO.answers;

import java.time.LocalDateTime;
import java.util.Set;

import com.ebs.boardparadice.model.BoardType;
import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.model.boards.Free;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FreeAnswerDTO {

	private int id;
	private String content;
	//gamer id번호만 전달
	private Gamer gamer;
	//free id번호만 전달
	private int free;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdate;
	
	private Set<Gamer> voter;
	
	private BoardType typeId;
}
