package com.ebs.boardparadice.model.boards;

import java.time.LocalDate;
import java.util.List;

import com.ebs.boardparadice.model.BoardType;
import com.ebs.boardparadice.model.Gamer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class History {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	private Integer id;
	
	private int win;
	private int draw;
	private int lose;
	
	private LocalDate date;
	
	@Column(nullable = false)
	private String content;
	
	@Column(nullable = false)
	private String title;
	
	@ManyToOne
	@JoinColumn(nullable = false, name = "gamer_id")
	private Gamer gamer;
	
	private List<String> mate;
	
	@ManyToOne
	@JoinColumn(nullable = false, name = "game_id")
	private Game game;
	
	private String filepath;
	private String filename;
	
	@ManyToOne
	@JoinColumn(name = "type_id")
	private BoardType type;	
}
