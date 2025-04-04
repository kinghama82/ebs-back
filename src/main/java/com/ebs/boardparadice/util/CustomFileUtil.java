package com.ebs.boardparadice.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnails;

@Component
@Log4j2
@RequiredArgsConstructor
public class CustomFileUtil {

	@Value("${upload.dir}")
	private String uploadPath;
	
	@PostConstruct
	public void init() {
		File temFolder = new File(uploadPath);
		
		if(temFolder.exists() == false) {
			temFolder.mkdirs();
		}
		uploadPath = temFolder.getAbsolutePath();
		
//		log.info("-----------------------------");
//		log.info("현재 저장되는 파일경로" + uploadPath);
//		log.info("-----------------------------");
		
	}
	
	//파일저장
	public List<String> saveFiles(List<MultipartFile> files) throws RuntimeException{
		if(files == null || files.size() == 0) {
			return List.of();
		}
		List<String> uploadNames = new ArrayList<>();
		
		for(MultipartFile multipartFile : files) {
			String savedName = UUID.randomUUID().toString() + "_" + multipartFile.getOriginalFilename();
			Path savePath = Paths.get(uploadPath, savedName);
			try {
				Files.copy(multipartFile.getInputStream(), savePath);
				String contentType = multipartFile.getContentType();
				if(contentType != null && contentType.startsWith("image")) {
					Path thumbnailPath = Paths.get(uploadPath, "s_"+savedName);
					Thumbnails.of(savePath.toFile())
							.size(200, 200)
							.toFile(thumbnailPath.toFile());
				}
				uploadNames.add(savedName);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		return uploadNames;
	}
	
	//파일조회
	public ResponseEntity<Resource> getFile(String fileName){
		Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
		
		if(!resource.isReadable()) {
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
	
	//파일 삭제
	public void deleteFiles(List<String> fileNames) {
		if(fileNames == null || fileNames.size() == 0) {
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
