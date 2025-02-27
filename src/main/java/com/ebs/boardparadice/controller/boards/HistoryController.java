package com.ebs.boardparadice.controller.boards;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebs.boardparadice.DTO.PageRequestDTO;
import com.ebs.boardparadice.DTO.PageResponseDTO;
import com.ebs.boardparadice.DTO.boards.HistoryDTO;
import com.ebs.boardparadice.service.boards.HistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

	private final HistoryService historyService;
	
	//기록 등록
	@PostMapping("/")
	public Map<String, String> create(@RequestBody HistoryDTO historyDTO){
		Integer id = historyService.createHistory(historyDTO);
		return Map.of("result", "등록 성공");
	}
	
	//기록 상세 열람
	@GetMapping("/{id}")
	public HistoryDTO get(@PathVariable(name = "id")Integer id) {
				
		return historyService.getHistory(id);
	}
	
	//목록(히스토리메인페이지)
	@GetMapping("/")
	public PageResponseDTO<HistoryDTO> list(PageRequestDTO pageRequestDTO){
		return historyService.getList(pageRequestDTO);
	}
	
	//삭제
	@DeleteMapping("/{id}")
	public Map<String, String> delete(@PathVariable(name = "id")Integer id){
		historyService.deleteHistory(id);
		return Map.of("result", "삭제 성공");
	}
	//수정
	@PutMapping("/{id}")
	public Map<String, String> modify(@PathVariable(name = "id")int id, @RequestBody HistoryDTO historyDTO){
		historyDTO.setId(id);
		historyService.modifyHistory(historyDTO);
		return Map.of("result1", "수정 성공");
	}
	
}
