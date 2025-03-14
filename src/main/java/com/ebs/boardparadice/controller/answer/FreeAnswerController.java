package com.ebs.boardparadice.controller.answer;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebs.boardparadice.DTO.answers.FreeAnswerDTO;
import com.ebs.boardparadice.service.answers.FreeAnswerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/free/answers")
public class FreeAnswerController {

	private final FreeAnswerService freeAnswerService;
	
	//댓글 등록
	@PostMapping("/")
	public Map<String, String> create(@RequestBody FreeAnswerDTO dto){
		try {
			Integer id = freeAnswerService.createFreeAnswer(dto);
			return Map.of("result", "등록 성공", "answerId", id.toString());
		} catch (Exception e) {
			return Map.of("result", "등록 실패");
		}
		
	}
	//수정
	
	//삭제
	
}
