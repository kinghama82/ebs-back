package com.ebs.boardparadice.service.boards;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ebs.boardparadice.DTO.PageRequestDTO;
import com.ebs.boardparadice.DTO.PageResponseDTO;
import com.ebs.boardparadice.DTO.boards.FreeDTO;
import com.ebs.boardparadice.model.boards.Free;
import com.ebs.boardparadice.model.boards.FreeImage;
import com.ebs.boardparadice.repository.boards.FreeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FreeService {

   private final FreeRepository freeRepository;
   private final ModelMapper modelMapper;

   //글 작성
   public int createFree(FreeDTO freeDTO) {
	   Free free = dtoToEntity(freeDTO);
	   Free result = freeRepository.save(free);
	   
	   return result.getId();
   }
   
   //리스트
   public PageResponseDTO<FreeDTO> getList(PageRequestDTO pageRequestDTO){
	   Pageable pageable = PageRequest.of(
			   				pageRequestDTO.getPage() -1,
			   				pageRequestDTO.getSize(),
			   				Sort.by("id").descending());
	   
	   Page<Object[]> result = freeRepository.selectList(pageable);
	   
	   List<FreeDTO> dtoList = result.get().map(arr -> {
		   Free free = (Free) arr[0];
		   FreeImage freeImage = (FreeImage) arr[1];
		   
		   FreeDTO freeDTO = FreeDTO.builder()
				   				.id(free.getId())
				   				.title(free.getTitle())
				   				.content(free.getContent())
				   				.gamer(free.getGamer())
				   				.createdate(LocalDateTime.now())
				   				.build();
		   
		   if(freeImage != null) {
			   String imageStr = freeImage.getFileName();
			   freeDTO.setUploadFileNames(List.of());
		   }else {
			   freeDTO.setUploadFileNames(List.of());
		   }
		   return freeDTO;
	   }).collect(Collectors.toList());			  
	   
	   Long totalCount = result.getTotalElements();
	   
	   return PageResponseDTO.<FreeDTO>withAll()
			   				.dtoList(dtoList)
			   				.pageRequestDTO(pageRequestDTO)
			   				.totalCount(totalCount)
			   				.build();
   }
   
   //수정
   public void modifyFree(FreeDTO freeDTO) {
	   Optional<Free> result = freeRepository.findById(freeDTO.getId());
	   
	   Free free = result.orElseThrow();
	   
	   free.setContent(freeDTO.getContent());
	   free.setTitle(freeDTO.getTitle());
	   
	   
	   free.clearList();
	   List<String> uploadFileNames = freeDTO.getUploadFileNames();
	   if(uploadFileNames != null && uploadFileNames.size() > 0) {
		   uploadFileNames.stream().forEach(uploadName -> {
			   free.addImageString(uploadName);
		   });
	   }	   
	   freeRepository.save(free);
   }
   
   //읽기
   public FreeDTO getFree(int id) {
	   Optional<Free> result = freeRepository.selectOne(id);
	   Free free = result.orElseThrow();
	   
	   FreeDTO freeDTO = entityToDTO(free);
	   return freeDTO;
   }
   
   //삭제
   public void deleteFree(int id) {
	   freeRepository.deleteById(id);
   }

   //dto -> entity
   private Free dtoToEntity(FreeDTO freeDTO) {
	   Free free = Free.builder()
			   .id(freeDTO.getId())
			   .title(freeDTO.getTitle())
			   .gamer(freeDTO.getGamer())
			   .content(freeDTO.getContent())
			   .createdate(LocalDateTime.now())
			   .build();
	   
	   List<String> uploadFileNames = freeDTO.getUploadFileNames();
	   
	   if(uploadFileNames == null) {
		   return free;
	   }
	   uploadFileNames.stream().forEach(uploadName -> {
		   free.addImageString(uploadName);
	   });
	   return free;
   }
   //entity -> dto
   private FreeDTO entityToDTO(Free free) {
	   FreeDTO freeDTO = FreeDTO.builder()
			   .id(free.getId())
			   .title(free.getTitle())
			   .gamer(free.getGamer())
			   .content(free.getContent())
			   .createdate(free.getCreatedate())
			   .build();
	   List<FreeImage> imageList = free.getImageList();
	   
	   if(imageList == null || imageList.size() == 0) {
		   return freeDTO;
	   }
	   List<String> fileNameList = imageList.stream().map(freeImage ->
	   			freeImage.getFileName()).toList();
	   
	   freeDTO.setUploadFileNames(fileNameList);
	   return freeDTO;
   }
}
