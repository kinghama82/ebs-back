package com.ebs.boardparadice.controller.boards;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebs.boardparadice.DTO.boards.QuestionDTO;
import com.ebs.boardparadice.service.boards.QuestionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
@Log4j2
public class QuestionController {
	
	private final QuestionService questionService;
	
	//상세보기 + 댓리스트
	@GetMapping("/{id}")
	public ResponseEntity<QuestionDTO> get(@PathVariable(name = "id")int id){
		QuestionDTO dto = questionService.getQuestion(id);
		return ResponseEntity.ok(dto);
	}

}
