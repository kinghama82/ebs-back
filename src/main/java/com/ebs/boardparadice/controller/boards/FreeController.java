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
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import com.ebs.boardparadice.model.boards.Free;
import com.ebs.boardparadice.service.boards.FreeService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnails;

@RestController
@RequestMapping("/api/free")
@RequiredArgsConstructor
@Log4j2
public class FreeController {

	private final FreeService freeService;

	@Value("${upload.dir}/free")
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
	public ResponseEntity<FreeDTO> get(@PathVariable(name = "id") int id) {
		FreeDTO dto = freeService.getFree(id);
		return ResponseEntity.ok(dto);
	}
	//댓글만 소환
	@GetMapping("/{id}/answers")
	public ResponseEntity<List<AnswerDTO>> getAnswers(@PathVariable(name = "id")int id){
		FreeDTO dto = freeService.getFree(id);
		return ResponseEntity.ok(dto.getAnswerList());
	}
	

	// 리스트
	@GetMapping("/")
	public PageResponseDTO<FreeDTO> list(PageRequestDTO pageRequestDTO) {
		log.info("list객체확인 : " + pageRequestDTO);
		return freeService.getList(pageRequestDTO);
	}

	// 수정
	@PutMapping("/{id}")
	public Map<String, String> modify(@PathVariable(name = "id") int id, FreeDTO freeDTO) {
		freeDTO.setId(id);
		FreeDTO oldFreedDto = freeService.getFree(id);
		
		List<String> oldFileNames = oldFreedDto.getUploadFileNames();
		List<MultipartFile> files = freeDTO.getFiles();
		List<String> currentUploadFileNames = saveFiles(files);
		List<String> uploadFileNames = freeDTO.getUploadFileNames();
		
		if(currentUploadFileNames != null && currentUploadFileNames.size() > 0) {
			uploadFileNames.addAll(currentUploadFileNames);
		}
		
		freeDTO.setUploadFileNames(uploadFileNames);
		freeService.modifyFree(freeDTO);
		
		if(oldFileNames != null && oldFileNames.size() > 0) {
			List<String> removeFiles = oldFileNames.stream()
					.filter(fileName -> uploadFileNames.indexOf(fileName) == -1)
					.collect(Collectors.toList());
			deleteFiles(removeFiles);
		}
		
		return Map.of("result", "수정 성공");
	}

	// 삭제
	@DeleteMapping("/{id}")
	public Map<String, String> delete(@PathVariable(name = "id") int id) {
		freeService.deleteFree(id);
		return Map.of("result", "삭제 성공");
	}
	
	//이미지보기
	@GetMapping("/view/{fileName}")
	public ResponseEntity<Resource> viewFileGet(@PathVariable(name = "fileName") String fileName){
		
		return getFile(fileName);
	}

	// 등록
	@PostMapping(value = "/", consumes = { "application/json" })
	public ResponseEntity<Map<String, String>> create(@RequestBody FreeDTO freeDTO) {
		if (freeDTO.getGamer() == null || freeDTO.getGamer().getId() == 0) {
	        return ResponseEntity.badRequest().body(Map.of("error", "작성자 정보 없음"));
	    }

	    try {
	        int id = freeService.createFree(freeDTO);
	        return ResponseEntity.ok(Map.of("result", "등록 성공"));
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
	    }
	}
	//조회수 top5
	@GetMapping("/view5")
	public ResponseEntity<List<FreeDTO>> getView5(){
		return ResponseEntity.ok(freeService.getViewTop5());
	}
	//추천수 top5
	@GetMapping("/vote5")
	public ResponseEntity<List<FreeDTO>> getVote5(){
		return ResponseEntity.ok(freeService.getVoteTop5());
	}
	
	//조회수 증가
	@GetMapping("/{id}/view")
	public void plusView(@PathVariable(name = "id")int id){
		freeService.plusFreeView(id);
	}
	//추천수 증가
	@PostMapping("/{id}/vote")
	public ResponseEntity<String> plusVote(
				@PathVariable(name = "id") int freeId,
				@RequestParam(name = "gamerId") int gamerId){
		try {
			freeService.plusFreeVote(freeId, gamerId);
			return ResponseEntity.ok("추천수 증가");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	//이미지업로드
	@PostMapping(value = "/upload", consumes = { "multipart/form-data" })
	public ResponseEntity<List<String>> uploadFiles(
			@RequestParam(value = "files", required = false) List<MultipartFile> files) {
		List<String> uploadedFiles = new ArrayList<>();
		
		//파일이 없는 경우 정상 응답 반환
	    if (files == null || files.isEmpty()) {
	        return ResponseEntity.ok(uploadedFiles);
	    }
	    for (MultipartFile file : files) {
	        String savedName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
	        Path savePath = Paths.get(uploadPath, savedName);

	        try {
	            Files.copy(file.getInputStream(), savePath);
	            uploadedFiles.add( savedName );
	        } catch (IOException e) {
	            return ResponseEntity.internalServerError().build();
	        }
	    }

	    return ResponseEntity.ok(uploadedFiles);
	}
	
	@DeleteMapping("/deleteFiles")
	public ResponseEntity<String> deleteFile(@RequestBody List<String> fileNames) {
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
