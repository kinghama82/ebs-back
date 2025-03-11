package com.ebs.boardparadice.controller.boards;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ebs.boardparadice.DTO.PageRequestDTO;
import com.ebs.boardparadice.DTO.PageResponseDTO;
import com.ebs.boardparadice.DTO.boards.FreeDTO;
import com.ebs.boardparadice.service.boards.FreeService;
import com.ebs.boardparadice.util.CustomFileUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/free")
@RequiredArgsConstructor
@Log4j2
public class FreeController {

	private final FreeService freeService;
	private final CustomFileUtil fileUtil;

	// 상세보기
	@GetMapping("/{id}")
	public FreeDTO get(@PathVariable(name = "id") int id) {
		return freeService.getFree(id);
	}

	// 목록
	@GetMapping("/")
	public PageResponseDTO<FreeDTO> list(PageRequestDTO pageRequestDTO) {
		log.info("list객체확인 : " + pageRequestDTO);
		return freeService.getList(pageRequestDTO);
	}

	// 수정
	@PutMapping("/{id}")
	public Map<String, String> modify(@PathVariable(name = "id") int id, @RequestBody FreeDTO freeDTO) {
		freeDTO.setId(id);
		freeService.modifyFree(freeDTO);
		return Map.of("result", "수정 성공");
	}

	// 삭제
	@DeleteMapping("/{id}")
	public Map<String, String> delete(@PathVariable(name = "id") int id) {
		freeService.deleteFree(id);
		return Map.of("result", "삭제 성공");
	}

	// 등록
	@PostMapping(value = "/", consumes = { "multipart/form-data" })
	public Map<String, String> create(@ModelAttribute FreeDTO freeDTO) {
		try {
			List<MultipartFile> files = freeDTO.getFiles();
			List<String> uploadFileNames = fileUtil.saveFiles(files);
			freeDTO.setUploadFileNames(uploadFileNames);
			int id = freeService.createFree(freeDTO);

			return Map.of("result", "등록 성공", "id", String.valueOf(id));
		} catch (Exception e) {
			return Map.of("result", "등록 실패", "error", e.getMessage());
		}

	}
}
