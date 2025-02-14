package com.ebs.boardparadice.service.boards;

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
import com.ebs.boardparadice.repository.boards.FreeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FreeService {

   private final FreeRepository freeRepository;
   private final ModelMapper modelMapper;

   //글 작성
   public int createFree(FreeDTO freeDTO) {
	   Free free = modelMapper.map(freeDTO, Free.class);
	   
	   Free saveFree = freeRepository.save(free);
	   
	   return saveFree.getId();
   }
   
   //리스트
   public PageResponseDTO<FreeDTO> getList(PageRequestDTO pageRequestDTO){
	   Pageable pageable = PageRequest.of(
			   				pageRequestDTO.getPage() -1,
			   				pageRequestDTO.getSize(),
			   				Sort.by("id").descending());
	   
	   Page<Free> result = freeRepository.findAll(pageable);
	   
	   List<FreeDTO> dtoList = result.getContent().stream()
			   .map(free -> modelMapper.map(free, FreeDTO.class))
			   .collect(Collectors.toList());
	   
	   Long totalCount = result.getTotalElements();
	   
	   PageResponseDTO<FreeDTO> responseDTO = PageResponseDTO.<FreeDTO>withAll()
			   									.dtoList(dtoList)
			   									.pageRequestDTO(pageRequestDTO)
			   									.totalCount(totalCount)
			   									.build();
	   return responseDTO;
   }
   
   //수정
   public void modifyFree(FreeDTO freeDTO) {
	   Optional<Free> result = freeRepository.findById(freeDTO.getId());
	   
	   Free free = result.orElseThrow();
	   
	   free.setContent(freeDTO.getContent());
	   free.setTitle(freeDTO.getTitle());
	   
	   
	   freeRepository.save(free);
   }
   public FreeDTO getFree(int id) {
	   Optional<Free> result = freeRepository.findById(id);
	   Free free = result.orElseThrow();
	   
	   FreeDTO dto = modelMapper.map(free, FreeDTO.class);
	   return dto;
   }
   
   //삭제
   public void deleteFree(int id) {
	   freeRepository.deleteById(id);
   }

}
