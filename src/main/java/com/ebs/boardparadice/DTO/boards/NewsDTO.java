package com.ebs.boardparadice.DTO.boards;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.ebs.boardparadice.DTO.answers.AnswerDTO;
import com.ebs.boardparadice.model.Gamer;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor // ✅ 기본 생성자 추가
@AllArgsConstructor // ✅ 모든 필드를 포함하는 생성자 추가
public class NewsDTO {
	private int id;
	private String title;

	private String content;
	private Gamer gamer;

	private List<AnswerDTO> answerList;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdate;

	private Set<Gamer> voter;

	@Builder.Default
	private List<String> uploadFileNames = new ArrayList<>();

	@Builder.Default
	private List<MultipartFile> files = new ArrayList<>();

	private int view;

	private String youtubeUrl;

}
