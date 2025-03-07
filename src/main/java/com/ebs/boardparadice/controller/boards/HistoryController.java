package com.ebs.boardparadice.controller.boards;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

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
	public Map<String, Integer> create(@ModelAttribute HistoryDTO historyDTO){
		Integer id = historyService.createHistory(historyDTO);
		return Map.of("result", id);
	}
	
    private String saveImageFile(MultipartFile imgFile) throws Exception {
        // ✅ 프로젝트 루트 경로 기준으로 static/uploads/games 폴더 설정
        String projectDir = System.getProperty("user.dir");
        Path uploadPath = Paths.get(projectDir, "src", "main", "resources", "static", "uploads", "history");

        // ✅ uploads/games 폴더가 없으면 생성
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            System.out.println("✅ 업로드 디렉토리 생성됨: " + uploadPath.toString());
        }

        // ✅ 원본 파일명에서 확장자 포함한 전체 이름 가져오기
        String originalFileName = imgFile.getOriginalFilename();

        // ✅ UUID + 원본 파일명 조합하여 저장 (공백 제거)
        String fileName = UUID.randomUUID().toString() + "_" + originalFileName.replaceAll("\\s+", "");

        Path filePath = uploadPath.resolve(fileName);
        System.out.println("🟢 파일 저장 시도 중: " + filePath.toString());

        try {
            Files.copy(imgFile.getInputStream(), filePath);
            System.out.println("✅ 파일 저장 완료: " + filePath.toString());
        } catch (Exception e) {
            System.err.println("🚨 파일 저장 오류: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        // ✅ 저장된 파일의 경로를 반환 (웹에서 접근할 수 있도록 상대 경로 사용)
        return "/uploads/history/" + fileName;
    }
	
	//기록 상세 열람
	@GetMapping("/read/{id}")
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
	public Map<String, String> modify(@PathVariable(name = "id")int id, @ModelAttribute HistoryDTO historyDTO){
		historyDTO.setId(id);
		historyService.modifyHistory(historyDTO);
		return Map.of("result1", "수정 성공");
	}
	
}
