package com.ebs.boardparadice.controller.boards;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ebs.boardparadice.DTO.PageRequestDTO;
import com.ebs.boardparadice.DTO.PageResponseDTO;
import com.ebs.boardparadice.DTO.answers.AnswerDTO;
import com.ebs.boardparadice.DTO.boards.FreeDTO;
import com.ebs.boardparadice.DTO.boards.QuestionDTO;
import com.ebs.boardparadice.service.boards.QuestionService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnails;

@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
@Log4j2
public class QuestionController {
	
	private final QuestionService questionService;
	
	@Value("${upload.dir}/question")
	private String uploadPath;

	@PostConstruct
	public void init() {
		File temFolder = new File(uploadPath);

		if (temFolder.exists() == false) {
			temFolder.mkdirs();
		}
		uploadPath = temFolder.getAbsolutePath();
	}

	// 상세보기 + 댓리스트
	@GetMapping("/{id}")
	public ResponseEntity<QuestionDTO> get(@PathVariable(name = "id") int id) {
		QuestionDTO dto = questionService.getQuestion(id);
		return ResponseEntity.ok(dto);
	}
	//댓글만 소환
	@GetMapping("/{id}/answers")
	public ResponseEntity<List<AnswerDTO>> getAnswers(@PathVariable(name = "id")int id){
		QuestionDTO dto = questionService.getQuestion(id);
		return ResponseEntity.ok(dto.getAnswerList());
	}
	

	// 리스트
	@GetMapping("/")
	public PageResponseDTO<QuestionDTO> list(PageRequestDTO pageRequestDTO) {
		log.info("list객체확인 : " + pageRequestDTO);
		return questionService.getList(pageRequestDTO);
	}

	// 수정
	@PutMapping("/{id}")
	public ResponseEntity<String> modify(@PathVariable(name = "id") int id, @RequestBody QuestionDTO questionDTO) {
	    questionDTO.setId(id);
	    QuestionDTO oldFreeDTO = questionService.getQuestion(id);

	    List<String> oldFileNames = oldFreeDTO.getUploadFileNames();
	    List<String> newFileNames = questionDTO.getUploadFileNames();

	    log.info("기존 이미지: " + oldFileNames);
	    log.info("새 이미지: " + newFileNames);

	    // ✅ 기존 파일이 있고, 새 파일이 다르면 기존 파일 삭제
	    if (oldFileNames != null && !oldFileNames.isEmpty() &&
	        newFileNames != null && !newFileNames.isEmpty()) {

	        String oldFile = oldFileNames.get(0);
	        String newFile = newFileNames.get(0);

	        if (oldFile != null && newFile != null && !oldFile.equals(newFile)) { // ✅ null 체크 추가
	            Path oldFilePath = Paths.get(uploadPath, oldFile);
	            try {
	                Files.deleteIfExists(oldFilePath);
	                log.info("기존 이미지 삭제 완료: " + oldFilePath.toString());
	            } catch (IOException e) {
	                log.error("기존 이미지 삭제 실패: " + e.getMessage());
	            }
	        }
	    }

	    // ✅ 새로운 이미지 파일명 저장 (항상 1개만 유지)
	    if (newFileNames != null && newFileNames.size() > 1) {
	        questionDTO.setUploadFileNames(newFileNames.subList(0, 1));
	    }

	    questionService.modifyQuestion(questionDTO);
	    return ResponseEntity.ok("수정 성공");
	}



	// 삭제
	@DeleteMapping("/{id}")
	public Map<String, String> delete(@PathVariable(name = "id") int id) {
		questionService.deleteFree(id);
		return Map.of("result", "삭제 성공");
	}
	
	//이미지보기
	@GetMapping("/view/{fileName}")
	public ResponseEntity<Resource> viewFileGet(@PathVariable(name = "fileName") String fileName){
		
		return getFile(fileName);
	}

	// 등록
	@PostMapping(value = "/", consumes = { "application/json" })
	public ResponseEntity<String> create(@RequestBody QuestionDTO questionDTO) {
	    log.info("📩 받은 payload: " + questionDTO);
	    log.info("📸 업로드된 파일 목록: " + questionDTO.getUploadFileNames());

	    if (questionDTO.getGamer() == null || questionDTO.getGamer().getId() == 0) {
	        return ResponseEntity.badRequest().body("작성자 정보 없음");
	    }

	    // ✅ 항상 1개의 파일만 유지
	    if (questionDTO.getUploadFileNames() != null && questionDTO.getUploadFileNames().size() > 1) {
	        questionDTO.setUploadFileNames(questionDTO.getUploadFileNames().subList(0, 1));
	    }

	    try {
	        questionService.createQuestion(questionDTO);
	        return ResponseEntity.ok("등록 성공");
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body(e.getMessage());
	    }
	}

	//조회수 top5
	@GetMapping("/view5")
	public ResponseEntity<List<QuestionDTO>> getView5(){
		return ResponseEntity.ok(questionService.getViewTop5());
	}
	//추천수 top5
	@GetMapping("/vote5")
	public ResponseEntity<List<QuestionDTO>> getVote5(){
		return ResponseEntity.ok(questionService.getVoteTop5());
	}
	
	//조회수 증가
	@GetMapping("/{id}/view")
	public void plusView(@PathVariable(name = "id")int id){
		questionService.plusFreeView(id);
	}
	//추천수 증가
	@PostMapping("/{id}/vote")
	public ResponseEntity<String> plusVote(
				@PathVariable(name = "id") int questionId,
				@RequestParam(name = "gamerId") int gamerId){
		try {
			questionService.plusFreeVote(questionId, gamerId);
			return ResponseEntity.ok("추천수 증가");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	//이미지업로드
	@PostMapping(value = "/upload", consumes = { "multipart/form-data" })
	public ResponseEntity<List<String>> uploadFiles(
			@RequestParam(value = "file", required = false) List<MultipartFile> files) {
		List<String> uploadedFiles = new ArrayList<>();
		
		//파일이 없는 경우 정상 응답 반환
	    if (files == null || files.isEmpty()) {
	    	log.warn("업로드할 파일이 없습니다.");
	        return ResponseEntity.ok(uploadedFiles);
	    }
	    for (MultipartFile file : files) {
	        String savedName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
	        Path savePath = Paths.get(uploadPath, savedName);

	        try {
	            log.info("파일 저장 시작: " + savePath.toString());
	            Files.copy(file.getInputStream(), savePath);
	            uploadedFiles.add(savedName);
	            log.info("파일 저장 완료: " + savedName);
	        } catch (IOException e) {
	            log.error("파일 저장 실패: " + e.getMessage());
	            return ResponseEntity.internalServerError().build();
	        }
	    }

	    return ResponseEntity.ok(uploadedFiles);
	}
	
	//이미지삭제
	@DeleteMapping("/deleteFiles")
	public ResponseEntity<String> deleteFile(@RequestParam(name = "fileNames") String fileName) {
		List<String> fileNames = List.of(fileName);
		deleteFiles(fileNames);
	    return ResponseEntity.ok("파일 삭제 완료");
	}




	// 파일저장
	public List<String> saveFiles(List<MultipartFile> files) throws RuntimeException {
		if (files == null || files.size() == 0) {
			return List.of();
		}
		List<String> uploadNames = new ArrayList<>();

		for (MultipartFile multipartFile : files) {
			String savedName = UUID.randomUUID().toString() + "_" + multipartFile.getOriginalFilename();
			Path savePath = Paths.get(uploadPath, savedName);
			try {
				Files.copy(multipartFile.getInputStream(), savePath);
				String contentType = multipartFile.getContentType();
				if (contentType != null && contentType.startsWith("image")) {
					Path thumbnailPath = Paths.get(uploadPath, "s_" + savedName);
					Thumbnails.of(savePath.toFile()).size(200, 200).toFile(thumbnailPath.toFile());
				}
				uploadNames.add(savedName);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		return uploadNames;
	}

	// 파일조회
	public ResponseEntity<Resource> getFile(String fileName) {
		Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

		if (!resource.isReadable()) {
			resource = new FileSystemResource(uploadPath + File.separator + "123.jpg");
		}
		HttpHeaders headers = new HttpHeaders();
		try {
			headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
		return ResponseEntity.ok().headers(headers).body(resource);
	}

	// 파일 삭제
	public void deleteFiles(List<String> fileNames) {
		if (fileNames == null || fileNames.size() == 0) {
			return;
		}

		fileNames.forEach(fileName -> {
			String thumbnailFileName = "s_" + fileName;
			Path thumbnailPath = Paths.get(uploadPath, thumbnailFileName);
			Path filePath = Paths.get(uploadPath, fileName);
			try {
				Files.deleteIfExists(filePath);
				Files.deleteIfExists(thumbnailPath);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}
		});
	}

}
