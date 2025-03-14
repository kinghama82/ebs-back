package com.ebs.boardparadice.controller.answer;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebs.boardparadice.DTO.answers.FreeAnswerDTO;
import com.ebs.boardparadice.DTO.boards.FreeDTO;
import com.ebs.boardparadice.service.answers.FreeAnswerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/free/answers")
public class FreeAnswerController {

	private final FreeAnswerService freeAnswerService;
	
	//댓글 등록
	@PostMapping("/")
	public ResponseEntity<?> create(@RequestBody FreeAnswerDTO dto){
		try {
			Integer id = freeAnswerService.createFreeAnswer(dto);
			FreeAnswerDTO savedAnswer = freeAnswerService.getFreeAnswer(id);
			return ResponseEntity.ok(Map.of("result", "등록 성공","answer",savedAnswer));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("result", "등록 실패","error",e.getMessage()));
		}
		
	}
	//수정
	@PutMapping("/{id}")
	public Map<String, String> modify(@PathVariable("id")int id, FreeAnswerDTO freeAnswerDTO){
		
		freeAnswerService.modifyFreeAnswer(freeAnswerDTO);
		return Map.of("result", freeAnswerDTO.getId() + " 번 수정 완료");
	}
	//삭제
	
}
