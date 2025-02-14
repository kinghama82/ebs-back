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
import com.ebs.boardparadice.DTO.boards.FreeDTO;
import com.ebs.boardparadice.service.boards.FreeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/free")
@RequiredArgsConstructor
@Log4j2
public class FreeController {
	
	private final FreeService freeService;
	
	//상세보기
	@GetMapping("/{id}")
	public FreeDTO get(@PathVariable(name = "id")int id) {
		return freeService.getFree(id);
	}
	
	//목록
	@GetMapping("/list")
	public PageResponseDTO<FreeDTO> list(PageRequestDTO pageRequestDTO){
		log.info("list객체확인 : " + pageRequestDTO);
		return freeService.getList(pageRequestDTO);
	}
	
	//수정
	@PutMapping("/{id}")
	public Map<String, String> modify(@PathVariable(name = "id")int id, @RequestBody FreeDTO freeDTO){
		freeDTO.setId(id);
		freeService.modifyFree(freeDTO);
		return Map.of("result", "수정 성공");
	}
	
	//삭제
	@DeleteMapping("/{id}")
	public Map<String, String> delete(@PathVariable(name = "id")int id){
		freeService.deleteFree(id);
		return Map.of("result", "삭제 성공");
	}

	//등록
	@PostMapping("/")
	public Map<String, String> create(@RequestBody FreeDTO freeDTO){
		int id = freeService.createFree(freeDTO);
		return Map.of("result", "등록 성공");
		
		
	}
}
