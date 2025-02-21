package com.ebs.boardparadice.DTO.boards;

import java.time.LocalDate;
import java.util.Set;

import com.ebs.boardparadice.model.BoardType;
import com.ebs.boardparadice.model.Gamer;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class FreeDTO {
	
	private int id;
	private String title;
	private String content;
	private Gamer writerId;
	private String filename;
	private String filepath;
	private BoardType typeId;
	private LocalDate createdate;
	private Set<Gamer> voter;

}
