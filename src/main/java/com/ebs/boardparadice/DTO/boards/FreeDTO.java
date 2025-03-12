package com.ebs.boardparadice.DTO.boards;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.ebs.boardparadice.model.BoardType;
import com.ebs.boardparadice.model.Gamer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FreeDTO {
	
	private int id;
	private String title;
	
	private String content;
	private Gamer gamer;
	private BoardType typeId;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdate;
	
	private Set<Gamer> voter;
	
	@Builder.Default
	private List<String> uploadFileNames = new ArrayList<>();
	
	@Builder.Default
	private List<MultipartFile> files = new ArrayList<>();

}
