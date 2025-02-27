package com.ebs.boardparadice.DTO.boards;

import java.time.LocalDate;
import java.util.List;

import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.model.boards.Game;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class HistoryDTO {

	private Integer id;
	private int win;
	private int draw;
	private int lose;
	
	private String content;
	private String title;
	private Game game;
	
	private Gamer gamer;
	private List<String> mate;
	
	private String filepath;
	private String filename;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yy-MM-dd")
	private LocalDate date;
}
